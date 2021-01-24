<template>
  <div class="d-flex flex-column align-center">
    <v-data-table
        :headers="headers"
        :items="mappaInCostruzione.stati"
        :items-per-page="5"
    >
      <template v-slot:top>
        <v-row class="my-4">
          <h4 class="text-h6">Stati</h4>
          <v-spacer/>
          <v-btn color="primary" @click="showDialogStati = true">Inserisci</v-btn>
        </v-row>
      </template>

      <template v-slot:[`item.actions`]="{ item }">
        <v-btn icon @click="mappaInCostruzione.removeStato(item.nome)">
          <v-icon>mdi-delete</v-icon>
        </v-btn>
      </template>
    </v-data-table>

    <v-dialog max-width="700px" v-model="showDialogStati">
      <v-card>
        <v-card-title>Aggiungi nuovo stato</v-card-title>
        <v-card-text>
          <v-form v-model="formValid">
            <v-row>
              <v-col cols="6">
                <v-text-field label="Nome"
                              v-model="nuovoStato.nome"
                              :rules="[nomeValido]"
                />
              </v-col>
              <v-col cols="6">
                <v-select label="Continente"
                          v-model="nuovoStato.continente"
                          :items="mappaInCostruzione.continenti.map(c => c.nome)"
                          :rules="[v => !!v]"
                />
              </v-col>
            </v-row>
          </v-form>
          <span class="d-block text-caption red--text" v-if="maxStatoPerContinenteRaggiunto">
            Per questo continente hai già raggiunto il numero massimo di
           stati</span>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="primary" text @click="showDialogStati = false">
            Annulla
          </v-btn>
          <v-btn color="primary" text @click="aggiungiStato" :disabled="!formValid || maxStatoPerContinenteRaggiunto">
            OK
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { mapGetters } from "vuex";

export default {
    name: "StatiTable",
    data() {
        return {
            headers: [
                {
                    text: "Nome",
                    value: "nome"
                },
                {
                    text: "Continente",
                    value: "continente"
                },
                {
                    text: "Operazioni",
                    value: "actions"
                }
            ],
            showDialogStati: false,
            nuovoStato: { nome: "", continente: "" },
            formValid: false,
          numMaxStatiPerContinente: 12
        };
    },
    computed: {
        ...mapGetters(["mappaInCostruzione"]),
      maxStatoPerContinenteRaggiunto() {
          if (!this.nuovoStato.continente)
            return false;
          const nStati = this.mappaInCostruzione.stati.filter(s => s.continente === this.nuovoStato.continente).length
        return (nStati === this.numMaxStatiPerContinente)
      }
    },
    methods: {
        aggiungiStato() {
            this.mappaInCostruzione.addStato(this.nuovoStato.nome, this.nuovoStato.continente);
            this.nuovoStato = { nome: "", continente: "" };
            this.showDialogStati = false;
        },
        nomeValido(nome) {
            if (!nome) {
                return "Oggetto obbligatorio";
            }
            if (this.mappaInCostruzione.contieneStati(nome)) {
                return "Nome già utilizzato";
            }
            return true;
        }
    }
};
</script>
