#!/usr/bin/env python
from sklearn.base import BaseEstimator, TransformerMixin
from sklearn.preprocessing import OneHotEncoder

class DataFrameSelector(BaseEstimator, TransformerMixin):
    def __init__(self, types):
        self.types = types

    def fit(self, x, y=None):
        return self
    
    def transform(self, x, y=None):
        return x.select_dtypes(include=self.types).values


class Binarizer(BaseEstimator, TransformerMixin):
    def fit(self, X, y=None,**fit_params):
        return self
        
    def transform(self, X):
        return OneHotEncoder().fit_transform(X)
