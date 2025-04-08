package org.mio.progettoingsoft.responses;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlanetsResponse {
    /*
    {
       "advCard" : "PLANETS",
        "color" : "(player's color)"
        "landedPlanet" : "number of chosen planet"
        "discard" : "List<GoodInDepot>"
        "move" : "List<GoodInDepot>"
        "place" : "Map<GoodType , Depot>"
    }
    idea : quando il giocatore sceglie se atterrare gli chiedo se vuole scartare, spostare le merci, in caso affermativo
    gli mostro prima i depositi e dopo che ne ha selezionato uno gli mostro le merci che contiene ogni deposito, e seleziona
    la merce da rimuovere. In questo modo gli errori che può commettere da gestire sono minori. Una volta fatto questo gli faccio
    posizionare le merci nella stessa maniera. Una volta ultimata questa fase metto tutto in un json e lo spedisco al controller,
    il controller prende il json e lo deserializza : grazie al parametro advCards chiamerò il metodo specifico (che sarà anche
    visibile dalla superclasse in quanto lo definiamo e semplicemente non farà nulla) e chiameremo i vari metodi di planets.
    Resta il fatto che secondo me questo non è il modo giusto di operare in quanto gli stati della carta che il margara ha
    sempre menzionato non servono. Questa è solo un'osservazione a me va bene provare a procedere così anche se secondo me
    sarebbe meglio fare tutto step by step.
    */
    public PlanetsResponse(){

    }


}
