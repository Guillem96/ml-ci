import os
import pandas as pd
import numpy as np

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

def get_woriking_sets(csv_path):
    """ Makes partitions of housing data for ml algorithms
    Returns:
        [(Dataframe, DataFrame, DataFrame)]: [(Training set X, Training set Y, Test set)]
    """
    housing = load_data(csv_path)

    housing["income_cat"] = np.ceil(housing["median_income"] / 1.5)
    housing["income_cat"].where(housing["income_cat"] < 5.0, 5.0, inplace=True) # Which are larger than 5 will get 5.0 value
    
    split = StratifiedShuffleSplit(n_splits=1, test_size=0.2, random_state=42)
    for train_index, test_index in split.split(housing, housing["income_cat"]):
        strat_train_set = housing.loc[train_index]
        strat_test_set = housing.loc[test_index]

    # Should remove the income_cat once it has been used
    strat_test_set.drop("income_cat", axis=1, inplace=True)
    strat_train_set.drop("income_cat", axis=1, inplace=True)

    housing = strat_train_set.drop("median_house_value", axis=1)
    housing_labels = strat_train_set["median_house_value"].copy()

    return housing, housing_labels, strat_test_set