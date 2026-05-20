INSERT INTO drivers(name, nationality, url)
VALUES
    ('Julian Sokołowski', 'Poland', 'http://julsok1'),
    ('Max Verstappen', 'Netherlands', 'http://max1'),
    ('Lewis Hamilton', 'England', 'http://lewis1'),
    ('Charles Leclerc',  'Monaco', 'http://charles1');

INSERT INTO constructors(name)
VALUES
    ('ferrari'),
    ('Mercedes');

INSERT INTO circuits(name, country, url)
VALUES
    ('Tor Poznan', 'Poland', 'http://poznantor'),
    ('Circuit de Barcelona', 'Spain', 'http://catalunatrack');

INSERT INTO races(circuit_id, date, name)
VALUES
    (1, '2025-10-12', 'GP Poznan'),
    (2, '2025-11-12', 'GP Barcelona');

INSERT INTO qualifying(race_id, driver_id, constructor_id, position)
VALUES
    (1, 1, 1, 1),
    (1, 2, 2, 2),
    (1, 3, 2, 3),
    (1, 4, 1, 4),

    (2, 1, 1, 2),
    (2, 2, 2, 3),
    (2, 3, 2, 4),
    (2, 4, 1, 1);

INSERT INTO results(race_id, driver_id, constructor_id, grid, position, points)
VALUES
    (1, 1, 1, 1, 1, 25.0),
    (1, 2, 2, 2, 2, 18.0),
    (1, 3, 2, 3, 3, 15.0),
    (1, 4, 1, 4, 4, 12.0),


    (2, 1, 1, 2, 2, 18.0),
    (2, 2, 2, 3, 4, 12.0),
    (2, 3, 2, 4, 3, 15.0),
    (2, 4, 1, 1, 1, 25.0);