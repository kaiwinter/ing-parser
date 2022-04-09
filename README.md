# ING DiBa CSV-Parser

Importiert CSV-Dateien, die aus dem Online-Banking der ING exportiert wurden.
Umsätze können anhand von Filterkriterien (auf Auftraggeber und Verwendungszweck) gruppiert werden.
Auf diesen Gruppen können dann Summen für definierte Zeiträume gebildet werden.

## Features
- Liest .csv-Dateien ein, die aus dem Online Banking exportiert wurden
- Gruppiert die Lastschriften anhand konfigurierter Schlagworte (im Verwendungszweck oder Auftraggeber) zu Kategorien
- Ermöglicht Auswertung durch Summenbildung und Bildung von Untergruppen nach Monaten
- Erkennt, wenn konfigurierte Schlagworte aus mehreren Kategorien auf eine Buchung zutreffen und warnt entsprechend