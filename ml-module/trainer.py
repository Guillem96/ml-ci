#!/usr/bin/env python
# Std imports
import os
import pandas as pd
import numpy as np
# Sklearn imports
from sklearn.model_selection import StratifiedShuffleSplit, GridSearchCV
from sklearn.preprocessing import LabelBinarizer, StandardScaler
from sklearn.impute import SimpleImputer
from sklearn.pipeline import Pipeline, FeatureUnion
from sklearn.externals import joblib
from sklearn.metrics import mean_squared_error

# Models
from sklearn.ensemble import RandomForestRegressor
from sklearn.linear_model import *

# Custom classes imports
from custom_transformers import DataFrameSelector, CombinedAttributesAdder, CustomBinarizer
import trainer_utils as housing_utils

BEST_MODEL = RandomForestRegressor()

def get_best_hyperparameters():
    param_list = [
        {'n_estimators': [3, 10, 30], 'max_features': [2, 4, 6, 8]},
        {'bootstrap': [False], 'n_estimators': [3, 10] , 'max_features': [2, 3, 4]}
    ]

    grid_search = GridSearchCV(BEST_MODEL, param_list, cv=5, scoring='neg_mean_squared_error')

    grid_search.fit(housing_prepared, housing_labels)

    print(grid_search.best_params_)

    print(grid_search.best_estimator_)

    cvres = grid_search.cv_results_

    for mean_score, params in zip(cvres["mean_test_score"], cvres["params"]):
        print(np.sqrt(-mean_score), params)

    return grid_search


def train_model(train_X, train_y, model_name, **hyperparameters):
    """Trains the algorithm named {model_name}
    
    Arguments:
        train_X {Dataframe} -- Training data
        train_y {Dataframe} -- Labels
        model_name {string} -- Algorithm to be trained name
    Returns:
        Sklearn model -- Result of training
    """
    print("Training {}...".format(model_name))
    model = eval(model_name + "(**hyperparameters)", globals(), locals())
    model.fit(train_X, train_y)
    
    print("Done")

    return model

def evaluate_model(model, X_test, y_test):
    """Evaluate model and prints its results
    
    Arguments:
        model {Sklearn model} -- Trained model to be evaluated
        X_test {Dataframe} -- Test data
        y_test {Dataframe} -- Test labels
    """
    final_predictions = model.predict(X_test)

    final_mse = mean_squared_error(y_test, final_predictions)
    final_rmse = np.sqrt(final_mse)

    print(model.__class__.__name__ + "-> "  + "MSE:", final_mse, "RMSE:", final_rmse)


def training_stage(cfg_file):
    """Automatically resolve a ML problem
    
    Arguments:
        cfg_file {MlCiCfg} -- Config file
    """

    housing, housing_labels, x_test, y_test = \
                housing_utils.get_woriking_sets(cfg_file.data_set, cfg_file.test_rate, cfg_file.target)

    housing_num = housing.drop("ocean_proximity", axis=1)

    num_pipeline = Pipeline([ # Execute all fit_transform sequentially
        ('selector', DataFrameSelector(list(housing_num))), # Slect numerical attributes
        ('imputer', SimpleImputer(strategy="median")),
        ('attribs_adder', CombinedAttributesAdder()),
        ('std_scaler', StandardScaler())
    ])

    cat_pipeline = Pipeline([ # Execute all fit_transform sequentially
        ('selector', DataFrameSelector(["ocean_proximity"])), # Slect categorical attributes
        ('label_binarizer', CustomBinarizer())
    ])

    # Fusion pipelines
    full_pipeline = FeatureUnion(transformer_list=[
        ("num_pipeline", num_pipeline),
        ("cat_pipeline", cat_pipeline)
    ])

    housing_prepared = full_pipeline.fit_transform(housing)

    # After some test we decided that random forest is the best model for now
    # model = get_best_hyperparameters().best_estimator_
    
    for cfg_model in cfg_file.models:
        model = train_model(housing_prepared, housing_labels, 
                                cfg_model.name, **cfg_model.params)
        
        ### Evaluate against test set ###
        x_tested_prepared = full_pipeline.transform(x_test)

        evaluate_model(model, x_tested_prepared, y_test)