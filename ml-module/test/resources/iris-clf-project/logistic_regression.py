import driftai as dai

from sklearn.linear_model import LogisticRegression

@dai.run.single_run
class LogisticRegressionApproach(dai.RunnableApproach):

    @property
    def parameters(self):
        return [
            dai.parameters.FloatParameter('tol', 1e-4, 1e-3, 5),
            dai.parameters.BoolParameter('fit_intercept')
        ]

    def learn(self, data, parameters):
        return LogisticRegression(**parameters).fit(**data)

    def inference(self, model, data):
        return model.predict(data['X'])