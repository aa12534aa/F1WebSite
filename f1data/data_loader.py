import pandas as pd
import numpy as np
import csv

"""Load all necessary CSV files."""
files = [
    'races', 'results', 'drivers', 'constructors', 'qualifying', 'circuits'
]

race_columns = ['raceId', 'circuitId', 'date', 'name']

results_columns = ['resultId', 'raceId', 'driverId', 'constructorId', 'grid', 'position', 'points']

drivers_columns = ['driverId', 'forename', 'surname', 'url']

constructors_columns = ['constructorId', 'name']

qualifying_columns = ['qualifyId', 'raceId', 'driverId', 'constructorId', 'position']

circuits_columns = ['circuitId', 'name', 'country', 'url']

data = {}

for file in files:
    data[file] = pd.read_csv(f'E:\\praca\\java\\SpringBoot\\F1WebSite\\f1data\\data\\{file}.csv', encoding='utf-8')
    
cleared_data = {}
cleared_data['races'] = data['races'][race_columns]
cleared_data['results'] = data['results'][results_columns]
cleared_data['drivers'] = data['drivers'][drivers_columns]
cleared_data['constructors'] = data['constructors'][constructors_columns]
cleared_data['qualifying'] = data['qualifying'][qualifying_columns]
cleared_data['circuits'] = data['circuits'][circuits_columns]

cleared_data['drivers']['name'] = cleared_data['drivers']['forename'] + ' ' + cleared_data['drivers']['surname']
cleared_data['drivers'].drop(['forename', 'surname'], axis='columns', inplace=True)

for file in files:
    cleared_data[file].to_csv(f'E:\\praca\\java\\SpringBoot\\F1WebSite\\f1data\\cleared_data\\cleared_{file}.csv', index=False, encoding='utf-8')