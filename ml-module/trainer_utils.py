import os
import pandas as pd
import numpy as np

from sklearn.model_selection import train_test_split
from sklearn.model_selection import StratifiedShuffleSplit

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


def get_woriking_sets(csv_path, test_size, target):
    """Makes partitions of data for ml algorithms
    
    Arguments:
        csv_path {string} -- Dataset file path
        target {string} -- Target to be predicted
        test_size {float} -- Test set percentage over the whole data

    Returns:
        [(Dataframe, DataFrame, DataFrame, Dataframe)] --[(Training set X, Training set Y, Test set)]
    """

    df = load_data(csv_path)

    X = df.drop(target, axis=1).copy()
    y = df[target].copy()

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=test_size, random_state=42)

    return X_train, y_train, X_test, y_test