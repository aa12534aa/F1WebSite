F1WebSite
// zrobione
GET /api/home:
-topka kierowców (najwięcej zwycięstw, najwięcej poleposition)
-topka teamów (najwięcej zwycięstw, najwięcej poleposition)

// zrobione
GET /api/drivers:
-page wszystkich kierowców posortowanych po liczbie zwycięstw
-wyszukiwanie kierowców po imieniu+nazwisku
-wyszukiwanie poszczególnego kierowcy
-każdy kierowca ma przekierowanie do /drivers/{id}
-każdy kierowca ma liczbę zwycięstw

// zrobione
GET /api/drivers/{id}:
-liczba 1, 2, 3 miejsc
-liczba wyścigów
-łączna liczba punktów
-liczba poleposition
-tory na których kierowca ma najwięcej wygranych

// zrobione
GET /api/drivers/{id}/races:
-page ostatnich 10 wyścigów z (miejscem startowym, miejscem końcowym, punktami, rokiem wyścigu, nazwą wyścigu)
-wyszukiwanie wyścigów po kraju

// zrobione
POST /api/drivers:
-tworzenie kierowcy

// zrobione
GET /api/constructors:
-page wszystkich konstruktorów posortowanych po liczbie zwycięstw
-wyszukiwanie konstruktorów po nazwie
-każdy konstruktor ma przekierowanie do indywidualnego widoku ze
-każdy konstruktor ma liczbę zwycięstw

// zrobione
GET /api/constructors/{id}:
-liczba 1, 2, 3 miejsc
-liczba wyścigów
-łączna liczba punktów

// zrobione
GET /api/constructors/{id}/races:
-page wyścigów
-wyszukiwanie wyścigów po kraju

// zrobione
POST /api/constructors:
-tworzenie konstruktora

// zrobione
GET /api/circuits:
-tory z liczbą wyścigów

// zrobione
POST /api/circuits:
-tworzenie toru

może (/circuits/{id} lista zwycięzców w ostatnich wyścigach na danym torze, kierowca który najwięcej wygrał)
ale trzeba stworzyć tabele circuts i ją

// zrobione
GET /api/races:
-page wyścigów
-nazwa wyścigu
-data
-nazwa toru i kraj
-imię zwycięzcy
-nazwa zespołu który wygrał

// zrobione
GET /api/races/{id}
-nazwa wyścigu
-data
-informacje o torze
-lista results

// zrobione
POST /api/races:
-tworzenie wyścigu (na bazie toru)

// zrobione
POST /api/races/{id}/results
-tworzenie result (na bazie id wyścigu, kierowcy, konstruktora)

// zrobione
POST /api/races/{id}/qualifying
-tworzenie qualifying (na bazie id wyścigu, kierowcy, konstruktora)


struktura bazy danych
drivers:
-id
-name
-nationality
-url (unique)

constructors:
-id
-name (unique)

circuits:
-id
-name (unique)
-country
-url

races:
-id
-circuit_id
-date
-name

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