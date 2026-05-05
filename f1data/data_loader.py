import pandas as pd
import numpy as np
import csv

"""Load all necessary CSV files."""
files = [
    'races', 'results', 'drivers', 'constructors'
]

race_columns = ['raceId', 'year', 'name']

results_columns = ['resultId', 'raceId', 'driverId', 'constructorId', 'grid', 'position', 'points']

drivers_columns = ['driverId', 'forename', 'surname', 'url']

constructors_columns = ['constructorId', 'name']

data = {}

for file in files:
    data[file] = pd.read_csv(f'E:\\praca\\java\\SpringBoot\\F1WebSite\\f1data\\{file}.csv', encoding='utf-8')
    
cleared_data = {}
cleared_data['races'] = data['races'][race_columns]
cleared_data['results'] = data['results'][results_columns]
cleared_data['drivers'] = data['drivers'][drivers_columns]
cleared_data['constructors'] = data['constructors'][constructors_columns]

cleared_data['drivers']['name'] = cleared_data['drivers']['forename'] + ' ' + cleared_data['drivers']['surname']
cleared_data['drivers'].drop(['forename', 'surname'], axis='columns', inplace=True)

for file in files:
    cleared_data[file].to_csv(f'java/SpringBoot/F1WebSite/f1data/f1_{file}.csv', index=False, encoding='utf-8')