#import spacy
import pandas as pd
import numpy as np
import nltk

# library to normalize text
from normalization import normalize_corpus
from featureExtraction import build_feature_matrix
# use once?
#nltk.download('stopwords')
from nltk.tokenize.toktok import ToktokTokenizer
import re
from bs4 import BeautifulSoup
from contractions import CONTRACTION_MAP
import unicodedata

print ("BEGINING")
# uncomment if it is the first time 
#nltk.download('stopwords')
movie_data =  pd.read_csv('movieDescriptionDataSet.tsv', sep='\t')
#print (news_df.head(10))

#print (news_df.Genre.value_counts())

print ("Normalizing corpus")

#news_df['clean_text'] = normalize_corpus(news_df['Description'])
#norm_corpus = list(news_df['clean_text'])

#print (news_df.iloc[1][['Description', 'clean_text']].to_dict())


#news_df.to_csv('movieDescriptionDataSetProcessed.tsv', index=False, encoding='utf-8' , sep='\t')

#print ( " created file :: == movieDescriptionDataSetProcessed.tsv")

# Use normalized text for clustering

movie_titles = movie_data['Title'].tolist();
movie_synopses = movie_data['Description'].tolist()

norm_movie_synopses = normalize_corpus(movie_synopses,
                                       lemmatize=True,
                                       only_text_chars=True)

vectorizer, feature_matrix = build_feature_matrix(norm_movie_synopses,
                                                  feature_type='tfidf',
                                                  min_df=0.10, max_df=0.90,
                                                  ngram_range=(1, 2))

print (feature_matrix.shape) 


feature_names = vectorizer.get_feature_names()

# print sample features
print (feature_names[:20])


from sklearn.cluster import KMeans

def k_means(feature_matrix, num_clusters=5):
    km = KMeans(n_clusters=num_clusters,
                max_iter=10000)
    km.fit(feature_matrix)
    clusters = km.labels_
    return km, clusters

num_clusters = 7   
km_obj, clusters = k_means(feature_matrix=feature_matrix,
                           num_clusters=num_clusters)
movie_data['Cluster'] = clusters


from collections import Counter
# get the total number of movies per cluster
c = Counter(clusters)
print (c.items())



def get_cluster_data(clustering_obj, movie_data, 
                     feature_names, num_clusters,
                     topn_features=10):

    cluster_details = {}  
    # get cluster centroids
    ordered_centroids = clustering_obj.cluster_centers_.argsort()[:, ::-1]
    # get key features for each cluster
    # get movies belonging to each cluster
    for cluster_num in range(num_clusters):
        cluster_details[cluster_num] = {}
        cluster_details[cluster_num]['cluster_num'] = cluster_num
        key_features = [feature_names[index] 
                        for index 
                        in ordered_centroids[cluster_num, :topn_features]]
        cluster_details[cluster_num]['key_features'] = key_features
        
        movies = movie_data[movie_data['Cluster'] == cluster_num]['Title'].values.tolist()
        cluster_details[cluster_num]['movies'] = movies
    
    return cluster_details
        
       
    
def print_cluster_data(cluster_data):
    # print cluster details
    for cluster_num, cluster_details in cluster_data.items():
        print ('Cluster {} details:'.format(cluster_num))
        print ('-'*20)
        print ('Key features:', cluster_details['key_features'])
        print ('Movies in this cluster:')
        print (', '.join(cluster_details['movies']))
        print ('='*40)

cluster_data =  get_cluster_data(clustering_obj=km_obj,
                                 movie_data=movie_data,
                                 feature_names=feature_names,
                                 num_clusters=num_clusters,
                                 topn_features=5)         

print_cluster_data(cluster_data) 


print ("ENDING")