import spacy
import pandas as pd
import numpy as np
import nltk

# library to normalize text
import normalization
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
news_df =  pd.read_csv('movieDescriptionDataSet.tsv', sep='\t')
#print (news_df.head(10))

#print (news_df.Genre.value_counts())


news_df['clean_text'] = normalization.normalize_corpus(news_df['Description'])
norm_corpus = list(news_df['clean_text'])

print (news_df.iloc[1][['Description', 'clean_text']].to_dict())


news_df.to_csv('movieDescriptionDataSetProcessed.tsv', index=False, encoding='utf-8')


print ("ENDING")