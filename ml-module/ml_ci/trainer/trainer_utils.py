#!/usr/bin/env python
import os
import pandas as pd
from sklearn.model_selection import train_test_split

def load_data(path):
    """Read a csv file as Panda's DataFrame
    
    Args:
        path (String): Path of csv directory
        name (String): Name of csv file
    
    Returns:
        Panda's DataFrame: Data
    """
    csv_path = os.path.join(path)
    return pd.read_csv(csv_path)


def get_woriking_sets(cfg_file, dropna=False):
    """Makes partitions of data for ml algorithms
    
    Arguments:
        cfg_file - Configuration file from user

    Returns:
        [(Dataframe, DataFrame, DataFrame, Dataframe)] --[(Training set X, Training set Y, Test set)]
    """
    csv_path = cfg_file.data_set
    test_size = cfg_file.test_rate
    target = cfg_file.target

    df = load_data(csv_path)

    # Drop columns specified at config file
    print("Droping columns: {}".format(cfg_file.drop_columns))
    df.drop(cfg_file.drop_columns, axis=1, inplace=True)

    if dropna:
        df.dropna(axis=0, inplace=True)

    X = df.drop(target, axis=1).copy()
    y = df[target].copy()

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=test_size, random_state=42)

    return X_train, y_train, X_test, y_test