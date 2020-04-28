package it.uniba.di.gruppo17.util;

/** Classe con informazioni utente per gestione del profilo **/
public class UserProfile {

        private String nome;
        private String cognome;
        private String email;

        public UserProfile(String nome, String cognome, String email)
        {
            this.nome = nome;
            this.cognome = cognome;
            this.email = email;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public void setCognome(String cognome) {
            this.cognome = cognome;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getNome() {
            return nome;
        }

        public String getCognome() {
            return cognome;
        }

        public String getEmail() {
            return email;
        }
}
