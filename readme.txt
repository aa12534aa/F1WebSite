F1WebSite
// zrobione
/home:
-topka kierowców (najwięcej zwycięstw, najwięcej poleposition)
-topka teamów (najwięcej zwycięstw, najwięcej poleposition)

// zrobione
/drivers:
-lista wszystkich kierowców posortowanych po liczbie zwycięstw
-wyszukiwanie poszczególnego kierowcy
-każdy kierowca ma przekierowanie do /drivers/{id}
-każdy kierowca ma liczbę zwycięstw

// zrobione
/drivers/{id}:
-lista ostatnich 10 wyścigów z (miejscem startowym, miejscem końcowym, punktami, rokiem wyścigu, nazwą wyścigu)
-wyszukiwanie poszczególnego wyścigu kierowcy
-liczba 1, 2, 3 miejsc
-liczba wyścigów
-łączna liczba punktów
-liczba poleposition

-może (tor na którym kierowca ma najwięcej wygranych)

// zrobione
/consturctors:
-lista wszystkich konstruktorów posortowanych po liczbie zwycięstw
-wyszukiwanie poszczególnego konstruktora
-każdy konstruktor ma przekierowanie do indywidualnego widoku ze
-każdy konstruktor ma liczbę zwycięstw

// zrobione
/constructors/{id}:
-lista ostatnich wyścigów
-wyszukiwanie poszczególnego wyścigu zespołu
-liczba 1, 2, 3 miejsc
-liczba wyścigów
-łączna liczba punktów

/tracks:
-tory z liczbą wyścigów

może (/tracks/{id} lista zwycięzców w ostatnich wyścigach na danym torze)
ale trzeba stworzyć tabele circuts i ją zaimportować