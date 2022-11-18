Aufbau:
- Spielfeld (Hanabi)
  - n Spieler
    - n Karten
      - 1 Farbe
      - 1 Zahl
      - n mögliche Zahlen
      - n mögliche Farben
  - Kartenstapel (1 Ablagestapel, 1 Nachziehstapel, n Farbstapel)
    - n Karten
  - n Spielsteine (? Gewitterplättchen, ? Hinweisplättchen)

Klassen:
Hanabi
- Player
- Card


n = number
nC = numberCard
nI = numberInfo

n == nC
  n == nI
    nI = true
  n != nI
    nI = false

(n == nC && n == nI) || (nI != n && nI) ????

n != nC
  n == nI
    nI = false
  n != nI
    nI = nI
_______________________

hc = hintColor
cc = cardColor
ci = colorInfoIndex
cb = colorInfoBoolean

hc = RAINBOW <- Ignore
cc = RAINBOW

//TODO set RAINBOW-INFO to false, when card with only R and one other INFO get a color-hint with a different color
//TODO also set R-I to false, when card gets a hint with a different color
_____________________________

- wenn undo/redo gebraucht wird, notfalls eine Kopie der Handkarten vor jedem takeHint an den move hängen
  - so werden die bis dahin bekannten Informationen über die Karten gespeichert
- zum speichern des Spiels (csv?) mit statistischen Werten auch die Reihenfolge der Startkarten festhalten
  - so kann das gleiche Spiel zum Vergleich mehrmals gespielt werden
_____________________________

- Model und Control trennen
- Cardinfo erweitern
  - Booleans(color, number) in Wahrscheinlichkeiten umrechnen
    -> in Karten-Matrix umrechnen
    - Bei Nummern die Anzahl pro Farbe berücksichtigen
  - Bekannte Karten die ausgeschlossen werden können
    - Handkarten der anderen Spieler
    - Farbstapel Karten
    - Ablagestapel (alle)
    -> zu Kartenmatrix und mit vorheriger multiplizieren
_____________________________

KARTEN AN- & ABLAGE
Logik-Abwägungen:
- Farbe und Nummer einer Karte ist bekannt
  - Karte folgt der höchsten angelegten Karte dieser Farbe
    - > Karte kann angelegt werden
  - Karte liegt bereits auf Farbstapel
    - > Karte kann abgelegt werden
- Nummer einer Karte ist bekannt
  - Nummer folgt den höchsten angelegten Karten aller möglichen Farben
    - > Karte kann angelegt werden
  - Alle möglichen Farbkarten mit dieser Nummer liegen bereits auf Farbstapel
    - > Karte kann abgelegt werden
- Farbe einer Karte ist bekannt
  - Alle möglichen Nummern der Farbe liegen bereits auf Farbstapel
    - > Karte kann abgelegt werden

Statistische Abwägungen:
- Für jeden Spieler herausfinden:
  - Welche Karten liegen bereits auf dem Farbstapel? (allgemeingültig) [F]
  - Welche Karten liegen bereits auf dem Ablagestapel? (allgemeingültig) [A]
  - Welche Karten sind in den Händen der Mitspieler? (spielerspezifisch) [H]
- Einschränkung der möglichen Karten (CardInfo) anhand der restlichen verbleibenden Karten
  - > Sollte vor Logik-Abwägung passieren
- Bewertung von Karten-Optionen, falls keine eindeutige logische Option vorhanden ist

HINWEISE
- Vielleicht Anzahl an ausgeschlossenen Optionen als Bewertungskriterium?
- 