package it.engsoft.risiko.model;

enum Figura{
    Cannone,
    Fante,
    Cavallo,
    Jolly
};

public class CartaTerritorio {
    private Stato statoRappresentato;
    private Figura figura;


    //stato rappresentato
    public Stato getStatoRappresentato() {
        return statoRappresentato;
    }
    public void setStatoRappresentato(Stato statoRappresentato) {
        if(statoRappresentato==null)
            throw new RuntimeException("Stato rappresentato nullo");
        this.statoRappresentato = statoRappresentato;
    }

    //figura
    public Figura getFigura() {
        return figura;
    }
    public void setFigura(Figura figura) {
        if (figura==null)
            throw new RuntimeException("Figura nulla");
        this.figura = figura;
    }

    //ritorna il valore di truppe che un giocatore può ricevere utilizzando un tris di carte
    public int valoreTruppeTris(CartaTerritorio x, CartaTerritorio  y, CartaTerritorio z){
        //le tre carte sono cannoni
         if(x.figura==y.figura && y.figura==z.figura && z.figura==Figura.Cannone)
             return 4;
         //le tre carte sono fanti
         if(x.figura==y.figura && y.figura==z.figura && z.figura==Figura.Fante)
             return 6;
         //le tre carte sono cavalli
         if(x.figura==y.figura && y.figura==z.figura && z.figura==Figura.Cavallo)
             return 8;
         //le tre carte sono composte da un jolly e due diverse tra loro
         if(x.figura!=y.figura && y.figura!=z.figura && x.figura!=z.figura && (x.figura==Figura.Jolly|| y.figura==Figura.Jolly|| z.figura==Figura.Jolly))
            return 10;
         //le tre carte sono composte da un jolly e due uguali tra loro
         if((x.figura==y.figura && z.figura==Figura.Jolly) || (x.figura==z.figura && y.figura==Figura.Jolly) || (y.figura==z.figura && x.figura==Figura.Jolly))
            return 12;
         //qualsiasi altra combinazione non è un tris valido e ritornerà zero
         return 0;
     }

     //ritorna un extra se gli stati delle carte del tris appartengono al giocatore che le utilizza
    public int extraTruppeTris(CartaTerritorio x, CartaTerritorio  y, CartaTerritorio z){
        int risultato=0;
        for (int i=0; i<x.statoRappresentato.getProprietario().getStati().size(); i++) {
            if (x.statoRappresentato == x.statoRappresentato.getProprietario().getStati().get(i))
                risultato = risultato + 2;
            if (y.statoRappresentato == x.statoRappresentato.getProprietario().getStati().get(i))
                risultato = risultato + 2;
            if (z.statoRappresentato == x.statoRappresentato.getProprietario().getStati().get(i))
                risultato = risultato + 2;
            //i tre if sono da unire; provando a unirli mi dava problemi di sintassi ma senza poter testare il tutto,
            //per ora, restano cosi'
        }
        return risultato;
    }

    //ritorna il totale delle truppe ricevute per un dato tris di carte
    public int TruppeTris(CartaTerritorio x, CartaTerritorio  y, CartaTerritorio z){
        return valoreTruppeTris(x, y, z) + extraTruppeTris(x, y, z);
    }

}