#!/usr/bin/env python
# Std imports
import os
import pandas as pd
import numpy as np

# Sklearn imports
from sklearn.model_selection import StratifiedShuffleSplit, GridSearchCV
from sklearn.externals import joblib
from sklearn.metrics import mean_squared_error

# Models
from sklearn.ensemble import *
from sklearn.linear_model import *
from sklearn.tree import *

# Custom classes imports
from ml_ci.trainer.trainer_utils import get_woriking_sets
from ml_ci.trainer.generic_pipelines import full_pipeline

# Webservice communication
from ml_ci.network import Network


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

def evaluate_model(model, X_test, y_test, is_regression):
    """Evaluate model and prints its results
    
    Arguments:
        model {Sklearn model} -- Trained model to be evaluated
        X_test {Dataframe} -- Test data
        y_test {Dataframe} -- Test labels
    """
    final_predictions = model.predict(X_test)

    if is_regression:
        final_mse = mean_squared_error(y_test, final_predictions)
        final_rmse = np.sqrt(final_mse)

        print(model.__class__.__name__ + "-> "  + "MSE:", final_mse, "RMSE:", final_rmse)
    else:
        print(model.__class__.__name__ + " -> "  + "Accuracy:", accuracy_score(y_test, y_pred))


def create_models(webservice, models):
    print("Creating models...")
    for m in models:
        print("- Creating model: " + m.name)
        webservice.create_model(m)
    print("Done")


def training_stage(cfg_file, webservice):
    """Automatically resolve a ML problem
    
    Arguments:
        cfg_file {MlCiCfg} -- Config file
    """

    # Create models at webservice
    webservice.authenticate()
    create_models(webservice, cfg_file.models)

    # Train test split
    X, y, x_test, y_test = \
                get_woriking_sets(cfg_file.data_set, cfg_file.test_rate, cfg_file.target)

    pipeline = None
    # If custom pipeline is not defined use the generic one
    if not cfg_file.pipeline:
        pipeline = full_pipeline
    else:
        # TODO: Import and use custom pipeline
        pass

    X_prepared = pipeline.fit_transform(X)

    # After some test we decided that random forest is the best model for now
    # model = get_best_hyperparameters().best_estimator_
    
    for cfg_model in cfg_file.models:
        # Update status of model to training
        webservice.update_model_status(cfg_model, "TRAINING")

        try:
            model = train_model(X_prepared, y, 
                                    cfg_model.name, **cfg_model.params)
            
            ### Evaluate against test set ###
            x_tested_prepared = full_pipeline.transform(x_test)

            evaluate_model(model, x_tested_prepared, y_test, cfg_file.train_type)
            
            # Update status of model to training
            webservice.update_model_status(cfg_model, "TRAINED")
        except:
            # Update model status to error
            webservice.update_model_status(cfg_model, "ERROR")
