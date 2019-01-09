
#!/usr/bin/env python
from sklearn.preprocessing import LabelBinarizer
from sklearn.base import BaseEstimator, TransformerMixin
import numpy as np

rooms_ix, bedrooms_ix, population_ix, household_ix = 3, 4, 5, 6

class CombinedAttributesAdder(BaseEstimator, TransformerMixin):
    def __init__(self, add_bedrooms_per_rooms=True):
        self.add_bedrooms_per_rooms = add_bedrooms_per_rooms
    
    def fit(self, x, y=None):
        return self
    
    def transform(self, x, y=None):
        rooms_per_household = x[:, rooms_ix] / x[:, household_ix]
        population_per_household = x[:, population_ix] / x[:, household_ix]
        if self.add_bedrooms_per_rooms :
            bedrooms_per_room = x[:, bedrooms_ix] / x[:, rooms_ix]
            return np.c_[x, rooms_per_household, population_per_household, bedrooms_per_room]
        
        return np.c_[x, rooms_per_household, population_per_household]


class DataFrameSelector(BaseEstimator, TransformerMixin):
    def __init__(self, attribute_names):
        self.attribute_names = attribute_names

    def fit(self, x, y=None):
        return self
    
    def transform(self, x, y=None):
        return x[self.attribute_names].values

def top_k_attr(arr, k):
    return np.sort(np.argpartition(np.array(arr), -k)[-k:]) 

class BestAttributes(BaseEstimator, TransformerMixin):
    def __init__(self, importances, k):
        self.importances = importances
        self.k = k

    def fit(self, x, y=None):
        self.best_indices = np.sort(np.argpartition(np.array(self.importances), -self.k)[-self.k:]) 
        return self.best_indices
    
    def transform(self, x, y=None):
        return x[:, self.best_indices]


class CustomBinarizer(BaseEstimator, TransformerMixin):
    def fit(self, X, y=None,**fit_params):
        return self
        
    def transform(self, X):
        return LabelBinarizer().fit_transform(X)