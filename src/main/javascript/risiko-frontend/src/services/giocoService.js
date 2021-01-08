import _axios from "@/plugins/axios";

export default {
    nuovoGioco,
    inviaRinforzi,
    nuovoTurno
}

function nuovoGioco(configurazione) {
    return _axios.post("api/gioco", configurazione)
    // console.log(configurazione)
    // return {data: mockNuovoGioco }
}

function inviaRinforzi(rinforzi) {
    return _axios.post("api/rinforzi", rinforzi)
}

function nuovoTurno() {
    return _axios.get("api/inizia-turno")
    // return { data: mockNuovoTurno }
}
//
// let mockNuovoTurno = {
//     numero: 1,
//     giocatore: "mario",
//     armateContinenti: 10,
//     armateStati: 10
// }
//
// let mockNuovoGioco = {
//     nArmateIniziali: 16,
//     primoGiocatore: "mario",
//     giocatori: [
//         {
//             nome: "mario",
//             stati: [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 41 ],
//             obiettivo: "Conquista 24 territori"
//         },
//         {
//             nome: "luigi",
//             stati: [ 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 42 ],
//             obiettivo: "Conquista 24 territori"
//         },
//         {
//             nome: "piero",
//             stati: [ 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 ],
//             obiettivo: "Conquista 24 territori"
//         },
//         {
//             nome: "claudio",
//             stati: [ 31, 32, 33, 34, 35, 36, 37, 38, 39, 40 ],
//             obiettivo: "Conquista 24 territori"
//         }
//     ],
//     mappa: {
//         "id": 1,
//         "nome": "Risiko Classic",
//         "descrizione": "La classica mappa del Risiko",
//         "numMinGiocatori": 3,
//         "numMaxGiocatori": 6,
//         "continenti": [
//             {
//                 "id": 1,
//                 "nome": "Asia",
//                 "armateBonus": 7,
//                 "stati": [
//                     {
//                         "id": 1,
//                         "nome": "Giappone",
//                         "confinanti": [
//                             4,
//                             5
//                         ]
//                     },
//                     {
//                         "id": 2,
//                         "nome": "Cina",
//                         "confinanti": [
//                             4,
//                             7,
//                             8,
//                             9,
//                             10,
//                             11,
//                             12
//                         ]
//                     },
//                     {
//                         "id": 3,
//                         "nome": "Cita",
//                         "confinanti": [
//                             4,
//                             5,
//                             6,
//                             7
//                         ]
//                     },
//                     {
//                         "id": 4,
//                         "nome": "Mongolia",
//                         "confinanti": [
//                             1,
//                             2,
//                             3,
//                             5,
//                             7
//                         ]
//                     },
//                     {
//                         "id": 5,
//                         "nome": "Kamchatka",
//                         "confinanti": [
//                             1,
//                             3,
//                             4,
//                             6,
//                             24
//                         ]
//                     },
//                     {
//                         "id": 6,
//                         "nome": "Jacuzia",
//                         "confinanti": [
//                             3,
//                             5,
//                             7
//                         ]
//                     },
//                     {
//                         "id": 7,
//                         "nome": "Siberia",
//                         "confinanti": [
//                             2,
//                             3,
//                             4,
//                             6,
//                             8
//                         ]
//                     },
//                     {
//                         "id": 8,
//                         "nome": "Urali",
//                         "confinanti": [
//                             2,
//                             7,
//                             9,
//                             19
//                         ]
//                     },
//                     {
//                         "id": 9,
//                         "nome": "Afghanistan",
//                         "confinanti": [
//                             2,
//                             8,
//                             12,
//                             19
//                         ]
//                     },
//                     {
//                         "id": 10,
//                         "nome": "Siam",
//                         "confinanti": [
//                             2,
//                             11,
//                             23
//                         ]
//                     },
//                     {
//                         "id": 11,
//                         "nome": "India",
//                         "confinanti": [
//                             2,
//                             10,
//                             12
//                         ]
//                     },
//                     {
//                         "id": 12,
//                         "nome": "Medio Oriente",
//                         "confinanti": [
//                             9,
//                             11,
//                             14,
//                             19,
//                             38
//                         ]
//                     }
//                 ]
//             },
//             {
//                 "id": 2,
//                 "nome": "Europa",
//                 "armateBonus": 5,
//                 "stati": [
//                     {
//                         "id": 13,
//                         "nome": "Europa settentrionale",
//                         "confinanti": [
//                             14,
//                             15,
//                             16,
//                             17,
//                             19
//                         ]
//                     },
//                     {
//                         "id": 14,
//                         "nome": "Europa meridionale",
//                         "confinanti": [
//                             12,
//                             13,
//                             15,
//                             19,
//                             37,
//                             38
//                         ]
//                     },
//                     {
//                         "id": 15,
//                         "nome": "Europa occidentale",
//                         "confinanti": [
//                             13,
//                             14,
//                             16,
//                             37
//                         ]
//                     },
//                     {
//                         "id": 16,
//                         "nome": "Gran Bretagna",
//                         "confinanti": [
//                             13,
//                             15,
//                             17,
//                             18
//                         ]
//                     },
//                     {
//                         "id": 17,
//                         "nome": "Scandinavia",
//                         "confinanti": [
//                             13,
//                             16,
//                             18,
//                             19
//                         ]
//                     },
//                     {
//                         "id": 18,
//                         "nome": "Islanda",
//                         "confinanti": [
//                             16,
//                             17,
//                             26
//                         ]
//                     },
//                     {
//                         "id": 19,
//                         "nome": "Ucraina",
//                         "confinanti": [
//                             8,
//                             9,
//                             12,
//                             13,
//                             14,
//                             17
//                         ]
//                     }
//                 ]
//             },
//             {
//                 "id": 3,
//                 "nome": "Oceania",
//                 "armateBonus": 2,
//                 "stati": [
//                     {
//                         "id": 20,
//                         "nome": "Australia occidentale",
//                         "confinanti": [
//                             21,
//                             22,
//                             23
//                         ]
//                     },
//                     {
//                         "id": 21,
//                         "nome": "Australia orientale",
//                         "confinanti": [
//                             20,
//                             22
//                         ]
//                     },
//                     {
//                         "id": 22,
//                         "nome": "Nuova Guinea",
//                         "confinanti": [
//                             20,
//                             21,
//                             23
//                         ]
//                     },
//                     {
//                         "id": 23,
//                         "nome": "Indonesia",
//                         "confinanti": [
//                             10,
//                             20,
//                             22
//                         ]
//                     }
//                 ]
//             },
//             {
//                 "id": 4,
//                 "nome": "America del Nord",
//                 "armateBonus": 5,
//                 "stati": [
//                     {
//                         "id": 24,
//                         "nome": "Alaska",
//                         "confinanti": [
//                             5,
//                             25,
//                             27
//                         ]
//                     },
//                     {
//                         "id": 25,
//                         "nome": "Territori del Nord Ovest",
//                         "confinanti": [
//                             24,
//                             26,
//                             27,
//                             28
//                         ]
//                     },
//                     {
//                         "id": 26,
//                         "nome": "Groenlandia",
//                         "confinanti": [
//                             18,
//                             25,
//                             28,
//                             29
//                         ]
//                     },
//                     {
//                         "id": 27,
//                         "nome": "Alberta",
//                         "confinanti": [
//                             24,
//                             25,
//                             28,
//                             30
//                         ]
//                     },
//                     {
//                         "id": 28,
//                         "nome": "Ontario",
//                         "confinanti": [
//                             25,
//                             26,
//                             27,
//                             29,
//                             30,
//                             31
//                         ]
//                     },
//                     {
//                         "id": 29,
//                         "nome": "Quebec",
//                         "confinanti": [
//                             26,
//                             28,
//                             31
//                         ]
//                     },
//                     {
//                         "id": 30,
//                         "nome": "Stati Uniti Occidentali",
//                         "confinanti": [
//                             27,
//                             28,
//                             31,
//                             32
//                         ]
//                     },
//                     {
//                         "id": 31,
//                         "nome": "Stati Uniti Orientali",
//                         "confinanti": [
//                             28,
//                             29,
//                             30,
//                             32
//                         ]
//                     },
//                     {
//                         "id": 32,
//                         "nome": "America centrale",
//                         "confinanti": [
//                             30,
//                             31,
//                             35
//                         ]
//                     }
//                 ]
//             },
//             {
//                 "id": 5,
//                 "nome": "America del Sud",
//                 "armateBonus": 2,
//                 "stati": [
//                     {
//                         "id": 33,
//                         "nome": "Brasile",
//                         "confinanti": [
//                             34,
//                             36,
//                             37
//                         ]
//                     },
//                     {
//                         "id": 34,
//                         "nome": "Perù",
//                         "confinanti": [
//                             33,
//                             35,
//                             36
//                         ]
//                     },
//                     {
//                         "id": 35,
//                         "nome": "Venezuela",
//                         "confinanti": [
//                             33,
//                             34
//                         ]
//                     },
//                     {
//                         "id": 36,
//                         "nome": "Argentina",
//                         "confinanti": [
//                             33,
//                             34
//                         ]
//                     }
//                 ]
//             },
//             {
//                 "id": 6,
//                 "nome": "Africa",
//                 "armateBonus": 3,
//                 "stati": [
//                     {
//                         "id": 37,
//                         "nome": "Africa del Nord",
//                         "confinanti": [
//                             14,
//                             15,
//                             33,
//                             38,
//                             39,
//                             40
//                         ]
//                     },
//                     {
//                         "id": 38,
//                         "nome": "Egitto",
//                         "confinanti": [
//                             12,
//                             14,
//                             37,
//                             39
//                         ]
//                     },
//                     {
//                         "id": 39,
//                         "nome": "Africa orientale",
//                         "confinanti": [
//                             12,
//                             37,
//                             38,
//                             40,
//                             41,
//                             42
//                         ]
//                     },
//                     {
//                         "id": 40,
//                         "nome": "Congo",
//                         "confinanti": [
//                             37,
//                             39,
//                             42
//                         ]
//                     },
//                     {
//                         "id": 41,
//                         "nome": "Madagascar",
//                         "confinanti": [
//                             39,
//                             42
//                         ]
//                     },
//                     {
//                         "id": 42,
//                         "nome": "Africa del Sud",
//                         "confinanti": [
//                             39,
//                             40,
//                             41
//                         ]
//                     }
//                 ]
//             }
//         ]
//     }
// }