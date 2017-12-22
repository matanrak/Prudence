# Prudence
Prudence - My first attempt at learning algorithm

  This is my first attempt at a learning algorithm, it learns by either reciving text or by finding text on it's own.
Prudence can find text by itself in articles from news sites (that are set in an enum) and learn from those, it will not learn twice from the same article. 

  It learns by taking text and splitting it into sentences and analyzing each one on it's own thread, each word in the sentence is analyaized by it's position and the word that follows it, in addition the algorithm also analyzes the sentence structure and learns it.

  Later by using it's furmula it decides the reletive probability (it's not mathematical probability because it's not in the range of 0 - 1, but it is still reletive to the other words) for every known word to come after word X to come after it in a certian sentence structure, the answere will change drastcally based on the current sentence structure.

Example output file for the word 'He':

{
  "Word": "He",
  "Word_Pos": "short form of",
  "Word_Count": 568,
  "Total_Count": 371865,
  "Probability": "{\"was\":4.0160153E-6,\"said\":2.3757295E-6,\"is\":1.4080152E-6,\"has\":1.107001E-6,\"and\":1.0396776E-6,\"had\":8.083067E-7,\"also\":4.510951E-7,\"says\":3.2601187E-7,\"will\":3.1891025E-7,\"wrote\":3.1195066E-7,\"can\":2.738386E-7,\"would\":2.7308107E-7,\"did\":2.6631085E-7,\"added\":1.6617798E-7,\"May\":1.660833E-7,\"wanted\":1.4203246E-7,\"thought\":1.4037542E-7,\"could\":1.2858672E-7,\"should\":1.2157979E-7,\"told\":1.2044353E-7}"
}



Prudence: "sensible and careful when you make judgements and decisions; avoiding unnecessary risks" (Oxford dictionary.)

Matan Rak 2017.
