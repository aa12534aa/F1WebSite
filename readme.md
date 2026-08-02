### F1StatsHub Endpoints

### Drivers Controller

GET /api/home:
- top drivers (most wins, most pole positions)
- top constructors (most wins, most pole positions)

GET /api/drivers:
- paginated list of all drivers sorted by win count
- search drivers by first and last name
- fetch individual driver details
- redirect to /drivers/{id} for each driver
- total win count for each driver

GET /api/drivers/{id}:
- count of 1st, 2nd, and 3rd place finishes
- total race count
- total points accumulated
- total pole position count
- circuits where the driver has the most victories

GET /api/drivers/{id}/races:
- paginated list of the 10 most recent races (starting position, finishing position, points, race year, race name)
- search races by country

POST /api/drivers:
- create a new driver

DELETE /api/drivers/{id}:
- delete driver (soft delete if associated with any results/qualifying data; hard delete otherwise)

PUT /api/drivers/{id}:
- full update of an existing driver


### Constructors Controller

GET /api/constructors:
- paginated list of all constructors sorted by win count
- search constructors by name
- redirect to individual constructor view for each constructor
- total win count for each constructor

GET /api/constructors/{id}:
- count of 1st, 2nd, and 3rd place finishes
- total race count
- total points accumulated

GET /api/constructors/{id}/races:
- paginated list of races
- search races by country

POST /api/constructors:
- create a new constructor

DELETE /api/constructors/{id}:
- delete constructor (soft delete if associated with any results/qualifying data; hard delete otherwise)

PUT /api/constructors/{id}:
- full update of an existing constructor


### Circuits Controller

GET /api/circuits:
- circuits with total hosted race counts

GET /api/circuits/{id}:
- circuit name
- country
- url
- list of drivers with the most wins at the circuit

POST /api/circuits:
- create a new circuit

DELETE /api/circuits/{id}:
- delete circuit (soft delete if associated with any race; hard delete otherwise)

PUT /api/circuits/{id}:
- full update of an existing circuit


### Races Controller

GET /api/races:
- paginated list of races
- race name
- date
- circuit name and country
- winner's name
- winning team name

GET /api/races/{id}:
- race name
- date
- circuit information
- list of results

GET /api/races/{id}/results:
- driver results for a specific race

GET /api/races/{id}/qualifying:
- driver qualifying results for a specific race

POST /api/races:
- create a new race (based on circuit)

POST /api/races/{id}/results:
- create a result (based on race ID, driver, constructor)

POST /api/races/{id}/qualifying:
- create a qualifying result (based on race ID, driver, constructor)

DELETE /api/races/{id}:
- delete race (soft delete if associated with any results/qualifying data; hard delete otherwise)

DELETE /api/races/{raceId}/results/{id}:
- delete result (always hard delete)

DELETE /api/races/{raceId}/qualifying/{id}:
- delete qualifying result (always hard delete)

PUT /api/race/{id}:
- full update of an existing race

PUT /api/race/{raceId}/results/{id}:
- full update of an existing result

PUT /api/race/{raceId}/qualifying/{id}:
- full update of an existing qualifying result


### Database Schema

drivers:
- id
- name
- nationality
- url (unique)
- isDeleted

constructors:
- id
- name (unique)
- isDeleted

circuits:
- id
- name (unique)
- country
- url
- isDeleted

races:
- id
- circuit_id
- date
- name
- isDeleted

qualifying:
- id
- race_id
- driver_id
- constructor_id
- position

results:
- id
- race_id
- driver_id
- constructor_id
- grid
- position
- points

users:
-id
-firstname
-lastname
-email
-password
-role