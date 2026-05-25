F1WebSite endpointy

// drivers controller
GET /api/home:
-topka kierowców (najwięcej zwycięstw, najwięcej poleposition)
-topka teamów (najwięcej zwycięstw, najwięcej poleposition)

GET /api/drivers:
-page wszystkich kierowców posortowanych po liczbie zwycięstw
-wyszukiwanie kierowców po imieniu+nazwisku
-wyszukiwanie poszczególnego kierowcy
-każdy kierowca ma przekierowanie do /drivers/{id}
-każdy kierowca ma liczbę zwycięstw

GET /api/drivers/{id}:
-liczba 1, 2, 3 miejsc
-liczba wyścigów
-łączna liczba punktów
-liczba poleposition
-tory na których kierowca ma najwięcej wygranych

GET /api/drivers/{id}/races:
-page ostatnich 10 wyścigów z (miejscem startowym, miejscem końcowym, punktami, rokiem wyścigu, nazwą wyścigu)
-wyszukiwanie wyścigów po kraju

POST /api/drivers:
-tworzenie kierowcy

DELETE /api/drivers/{id}
-usuwanie kierowcy (jeżeli ma jakieś wyniki/kwalifikacje soft delete w przeciwnym wypadku hard delete)


// constructors controller
GET /api/constructors:
-page wszystkich konstruktorów posortowanych po liczbie zwycięstw
-wyszukiwanie konstruktorów po nazwie
-każdy konstruktor ma przekierowanie do indywidualnego widoku ze
-każdy konstruktor ma liczbę zwycięstw

GET /api/constructors/{id}:
-liczba 1, 2, 3 miejsc
-liczba wyścigów
-łączna liczba punktów

GET /api/constructors/{id}/races:
-page wyścigów
-wyszukiwanie wyścigów po kraju

POST /api/constructors:
-tworzenie konstruktora

DELETE /api/constructors/{id}
-usuwanie konstruktora (jeżeli ma jakieś wyniki/kwalifikacje soft delete w przeciwnym wypadku hard delete)


// circuits controller
GET /api/circuits:
-tory z liczbą wyścigów

GET /api/circuits/{id}:
-nazwa toru
-kraj
-url
-lista kierowców z największą liczbą wygranych na danym torze

POST /api/circuits:
-tworzenie toru

DELETE /api/circuits/{id}
-usuwanie toru (jeżeli ma jakiś wyścig soft delete w przeciwnym wypadku hard delete)


// races controller
GET /api/races:
-page wyścigów
-nazwa wyścigu
-data
-nazwa toru i kraj
-imię zwycięzcy
-nazwa zespołu który wygrał

GET /api/races/{id}
-nazwa wyścigu
-data
-informacje o torze
-lista results

GET /api/races/{id}/results
-rezultaty kierowców w danym wyscigu

GET /api/races/{id}/qualifying:
-kwalfikacje kierowców w danym wyścigu

POST /api/races:
-tworzenie wyścigu (na bazie toru)

POST /api/races/{id}/results
-tworzenie result (na bazie id wyścigu, kierowcy, konstruktora)

POST /api/races/{id}/qualifying
-tworzenie qualifying (na bazie id wyścigu, kierowcy, konstruktora)

DELETE /api/races/{id}
-usuwanie wyścigu (jeżeli ma jakieś wyniki/kwalifikacje soft delete w przeciwnym wypadku hard delete)

DELETE /api/races/{raceId}/results/{id}
-usuwanie wyniku zawsze hard delete

DELETE /api/races/{raceId}/qualifying/{id}
-usuwanie kwalfikacji zawsze hard delete



struktura bazy danych
drivers:
-id
-name
-nationality
-url (unique)
-isDeleted

constructors:
-id
-name (unique)
-isDeleted

circuits:
-id
-name (unique)
-country
-url
-isDeleted

races:
-id
-circuit_id
-date
-name
-isDeleted

qualifying:
-id
-race_id
-driver_id
-constructor_id
-position

results:
-id
-race_id
-driver_id
-constructor_id
-grid
-position
-points