const legendaCarteTerritorio = [
    {
        tris: "3 cannoni",
        armate: 4
    },
    {
        tris: "3 fanti",
        armate: 6
    },
    {
        tris: "3 cavalieri",
        armate: 8
    },
    {
        tris: "Un cannone, un fante, un cavaliere",
        armate: 10
    },
    {
        tris: "Due figure uguali e un jolly",
        armate: 12
    }
]

const trisValido = (carte) => {
    let nJolly = carte.filter(c => c.figura === "JOLLY").length
    let nCannoni = carte.filter(c => c.figura === "CANNONE").length
    let nFanti = carte.filter(c => c.figura === "FANTE").length
    let nCavalli = carte.filter(c => c.figura === "CAVALLO").length

    return (nJolly === 1 && (nCannoni === 2 || nFanti === 2 || nCavalli === 2)) ||
        nCannoni === 3 ||
        nFanti === 3 ||
        nCavalli === 3 ||
        (nCannoni === 1 && nFanti === 1 && nCavalli === 1)
}

export default {
    legendaCarteTerritorio,
    trisValido
}