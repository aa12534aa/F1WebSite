F1WebSite
// zrobione
/api/home:
-topka kierowców (najwięcej zwycięstw, najwięcej poleposition)
-topka teamów (najwięcej zwycięstw, najwięcej poleposition)

// zrobione
/api/drivers:
-page wszystkich kierowców posortowanych po liczbie zwycięstw
-wyszukiwanie kierowców po imieniu+nazwisku
-wyszukiwanie poszczególnego kierowcy
-każdy kierowca ma przekierowanie do /drivers/{id}
-każdy kierowca ma liczbę zwycięstw

// zrobione
/api/drivers/{id}:
-liczba 1, 2, 3 miejsc
-liczba wyścigów
-łączna liczba punktów
-liczba poleposition

// zrobione
/api/drivers/{id}/races
-page ostatnich 10 wyścigów z (miejscem startowym, miejscem końcowym, punktami, rokiem wyścigu, nazwą wyścigu)
-wyszukiwanie wyścigów po kraju

// zrobione
/api/drivers/{id}/best-circuit
-tor na którym kierowca ma najwięcej wygranych

// zrobione
/api/constructors:
-page wszystkich konstruktorów posortowanych po liczbie zwycięstw
-wyszukiwanie konstruktorów po nazwie
-każdy konstruktor ma przekierowanie do indywidualnego widoku ze
-każdy konstruktor ma liczbę zwycięstw

// zrobione
/api/constructors/{id}:
-liczba 1, 2, 3 miejsc
-liczba wyścigów
-łączna liczba punktów

// zrobione
/api/constructors/{id}/races:
-page ostatnich wyścigów
-wyszukiwanie wyścigów po kraju

// zrobione
/api/circuits:
-tory z liczbą wyścigów

może (/circuits/{id} lista zwycięzców w ostatnich wyścigach na danym torze, kierowca który najwięcej wygrał)
ale trzeba stworzyć tabele circuts i ją


struktura bazy danych
drivers:
-id
-name
-url

constructors:
-id
-name

circuits:
-id
-name
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