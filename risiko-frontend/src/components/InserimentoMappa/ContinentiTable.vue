<template>
  <div class="d-flex flex-column align-center">
    <!-- Tabella continenti -->
    <v-data-table
        :headers="headers"
        :items="mappaInCostruzione.continenti"
        :items-per-page="5"
    >
      <!-- Titolo e pulsante inserimento -->
      <template v-slot:top>
        <v-row class="my-4">
          <h4 class="text-h6">Continenti</h4>
          <v-spacer/>
          <span class="text-caption red--text" v-if="raggiuntoMaxContinenti">Numero massimo di continenti raggiunto</span>
          <v-btn color="primary" @click="showDialogContinenti = true" :disabled="raggiuntoMaxContinenti">Inserisci</v-btn>
        </v-row>
      </template>

      <!-- Slot: azioni -->
      <template v-slot:[`item.actions`]="{ item }">
        <v-btn icon :disabled="mappaInCostruzione.contieneStati(item.nome)"
               @click="mappaInCostruzione.removeContinente(item.nome)">
          <v-icon>mdi-delete</v-icon>
        </v-btn>
      </template>
    </v-data-table>

    <!-- Inserimento continente dialog -->
    <v-dialog max-width="700px" v-model="showDialogContinenti">
      <v-card>
        <v-card-title>Aggiungi nuovo continente</v-card-title>
        <v-card-text>
          <v-form v-model="formValid">
            <v-row>
              <v-col cols="6">
                <v-text-field label="Nome"
                              v-model="nuovoContinente.nome"
                              :rules="[nomeValido]"
                />
              </v-col>
              <v-col cols="6">
                <v-select label="Armate bonus"
                          v-model="nuovoContinente.armateBonus"
                          :items="[2, 3, 4, 5, 6, 7]"/>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="primary" text @click="showDialogContinenti = false">
            Annulla
          </v-btn>
          <v-btn color="primary" text @click="aggiungiContinente" :disabled="!formValid">
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
    name: "ContinentiTable",
    data() {
        return {
            headers: [
                {
                    text: "Nome",
                    value: "nome"
                },
                {
                    text: "Armate bonus",
                    value: "armateBonus"
                },
                {
                    text: "Operazioni",
                    value: "actions",
                    sortable: false
                }
            ],
            nuovoContinente: { nome: "", armateBonus: 3 },
            showDialogContinenti: false,
            formValid: false,
          numMaxContinenti: 8
        };
    },
    computed: {
        ...mapGetters(["mappaInCostruzione"]),
      raggiuntoMaxContinenti() {
          return this.mappaInCostruzione.continenti.length === this.numMaxContinenti
      }
    },
    methods: {
        nomeValido(nome) {
            if (!nome.trim()) {
                return "Oggetto obbligatorio";
            }
            if (this.mappaInCostruzione.continentePresente(nome)) {
                return "Nome già utilizzato";
            }
            return true;
        },
        aggiungiContinente() {
            this.mappaInCostruzione.addContinente(this.nuovoContinente.nome.trim(), this.nuovoContinente.armateBonus);
            this.nuovoContinente = { nome: "", armateBonus: 3 };
            this.showDialogContinenti = false;
        }
    }
};
</script>
