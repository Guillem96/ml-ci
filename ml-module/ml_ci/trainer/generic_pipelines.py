#!/usr/bin/env python
from trainer.generic_transformers import DataFrameSelector, Binarizer
from sklearn.pipeline import Pipeline, FeatureUnion
from sklearn.preprocessing import StandardScaler
from sklearn.impute import SimpleImputer

num_pipeline = Pipeline([ # Execute all fit_transform sequentially
    ('selector', DataFrameSelector(["int", "float"])), # Select numerical attributes
    ('imputer', SimpleImputer(strategy="median")),
    ('std_scaler', StandardScaler())
])

cat_pipeline = Pipeline([ # Execute all fit_transform sequentially
    ('selector', DataFrameSelector(["object"])), # Select categorical attributes
    ('label_binarizer', Binarizer())
])

# Fusion pipelines
full_pipeline = FeatureUnion(transformer_list=[
    ("num_pipeline", num_pipeline),
    ("cat_pipeline", cat_pipeline)
])