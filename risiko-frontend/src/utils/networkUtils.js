const groups = [{
    nome: "Blu chiaro",
    border: "#2B7CE9",
    background: "#97C2FC",
    highlight: { border: "#2B7CE9", background: "#D2E5FF" },
    hover: { border: "#2B7CE9", background: "#D2E5FF" }
}, {
    nome: "Giallo",
    border: "#FFA500",
    background: "#FFFF00",
    highlight: { border: "#FFA500", background: "#FFFFA3" },
    hover: { border: "#FFA500", background: "#FFFFA3" }
}, {
    nome: "Rosso chiaro",
    border: "#FA0A10",
    background: "#FB7E81",
    highlight: { border: "#FA0A10", background: "#FFAFB1" },
    hover: { border: "#FA0A10", background: "#FFAFB1" }
}, {
    nome: "Verde",
    border: "#41A906",
    background: "#7BE141",
    highlight: { border: "#41A906", background: "#A1EC76" },
    hover: { border: "#41A906", background: "#A1EC76" }
}, {
    nome: "Violetto",
    border: "#E129F0",
    background: "#EB7DF4",
    highlight: { border: "#E129F0", background: "#F0B3F5" },
    hover: { border: "#E129F0", background: "#F0B3F5" }
}, {
    nome: "Viola chiaro",
    border: "#7C29F0",
    background: "#AD85E4",
    highlight: { border: "#7C29F0", background: "#D3BDF0" },
    hover: { border: "#7C29F0", background: "#D3BDF0" }
}, {
    nome: "Arancione chiaro",
    border: "#C37F00",
    background: "#FFA807",
    highlight: { border: "#C37F00", background: "#FFCA66" },
    hover: { border: "#C37F00", background: "#FFCA66" }
}, {
    nome: "Blu",
    border: "#4220FB",
    background: "#6E6EFD",
    highlight: { border: "#4220FB", background: "#9B9BFD" },
    hover: { border: "#4220FB", background: "#9B9BFD" }
}, {
    nome: "Rosa chiaro",
    border: "#FD5A77",
    background: "#FFC0CB",
    highlight: { border: "#FD5A77", background: "#FFD1D9" },
    hover: { border: "#FD5A77", background: "#FFD1D9" }
}, {
    nome: "Verde chiaro",
    border: "#4AD63A",
    background: "#C2FABC",
    highlight: { border: "#4AD63A", background: "#E6FFE3" },
    hover: { border: "#4AD63A", background: "#E6FFE3" }
}, {
    nome: "Rosso acceso",
    border: "#990000",
    background: "#EE0000",
    highlight: { border: "#BB0000", background: "#FF3333" },
    hover: { border: "#BB0000", background: "#FF3333" }
}, {
    nome: "Arancione",
    border: "#FF6000",
    background: "#FF6000",
    highlight: { border: "#FF6000", background: "#FF6000" },
    hover: { border: "#FF6000", background: "#FF6000" }
}, {
    nome: "Blu mare",
    border: "#97C2FC",
    background: "#2B7CE9",
    highlight: { border: "#D2E5FF", background: "#2B7CE9" },
    hover: { border: "#D2E5FF", background: "#2B7CE9" }
}, {
    nome: "Verde acceso",
    border: "#399605",
    background: "#255C03",
    highlight: { border: "#399605", background: "#255C03" },
    hover: { border: "#399605", background: "#255C03" }
}, {
    nome: "Fucsia",
    border: "#B70054",
    background: "#FF007E",
    highlight: { border: "#B70054", background: "#FF007E" },
    hover: { border: "#B70054", background: "#FF007E" }
}, {
    nome: "Viola",
    border: "#AD85E4",
    background: "#7C29F0",
    highlight: { border: "#D3BDF0", background: "#7C29F0" },
    hover: { border: "#D3BDF0", background: "#7C29F0" }
}, {
    nome: "Blu scuro",
    border: "#4557FA",
    background: "#000EA1",
    highlight: { border: "#6E6EFD", background: "#000EA1" },
    hover: { border: "#6E6EFD", background: "#000EA1" }
}, {
    nome: "Rosa",
    border: "#FFC0CB",
    background: "#FD5A77",
    highlight: { border: "#FFD1D9", background: "#FD5A77" },
    hover: { border: "#FFD1D9", background: "#FD5A77" }
}, {
    nome: "Verde acqua",
    border: "#C2FABC",
    background: "#74D66A",
    highlight: { border: "#E6FFE3", background: "#74D66A" },
    hover: { border: "#E6FFE3", background: "#74D66A" }
}, {
    nome: "Cremisi",
    border: "#EE0000",
    background: "#990000",
    highlight: { border: "#FF3333", background: "#BB0000" },
    hover: { border: "#FF3333", background: "#BB0000" }
}];

const gameNetworkOptions = {
    groups: { useDefaultGroups: false },
    nodes: {
        shape: "dot",

        size: 30,
        font: {
            size: 38,
            color: "#ffffff",
            align: "center"
        },
        borderWidth: 2,

        chosen: {
            node(values) {
                values.size = 35;
                values.color = "black";
            }
        }
    },
    edges: {
        width: 2
    },
    interaction: {
        dragView: false,
        zoomView: false,
        dragNodes: false,
        hover: true,
        tooltipDelay: 0
    },
    physics: {
        solver: "forceAtlas2Based",
        stabilization: {
            iterations: 2000
        },
        forceAtlas2Based: {
            avoidOverlap: 1
        }
    }
};

export default {
    groups,
    gameNetworkOptions
};
