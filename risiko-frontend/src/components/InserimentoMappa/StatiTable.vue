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
          <v-btn color="primary" @click="showDialog = true">Inserisci</v-btn>
        </v-row>
      </template>

      <template v-slot:[`item.actions`]="{ item }">
        <v-btn icon @click="mappaInCostruzione.removeStato(item.nome)">
          <v-icon>mdi-delete</v-icon>
        </v-btn>
      </template>
    </v-data-table>

    <v-dialog max-width="700px" v-model="showDialog">
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
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="primary" text @click="showDialog = false">
            Annulla
          </v-btn>
          <v-btn color="primary" text @click="aggiungiStato" :disabled="!formValid">
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
            showDialog: false,
            nuovoStato: { nome: "", continente: "" },
            formValid: false
        };
    },
    computed: {
        ...mapGetters(["mappaInCostruzione"])
    },
    methods: {
        aggiungiStato() {
            this.mappaInCostruzione.addStato(this.nuovoStato.nome, this.nuovoStato.continente);
            this.nuovoStato = { nome: "", continente: "" };
            this.showDialog = false;
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

<style scoped>

</style>
