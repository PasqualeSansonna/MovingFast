package it.uniba.di.gruppo17.util;

/** Classe con informazioni utente per gestione del profilo **/
public class UserProfile {

        private int id;
        private boolean manutentore;
        private String nome;
        private String cognome;
        private String email;

        public UserProfile(int id, String nome, String cognome, String email, boolean manutentore)
        {
            this.nome = nome;
            this.cognome = cognome;
            this.email = email;
            this.id = id;
            this.manutentore = manutentore;
        }

        public void setId (int id) { this.id = id;}

        public void setNome(String nome) {
            this.nome = nome;
        }

        public void setCognome(String cognome) {
            this.cognome = cognome;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setManutentore (boolean manutentore) { this.manutentore = manutentore;}

        public int getId() { return  id;}

        public String getNome() {
            return nome;
        }

        public String getCognome() {
            return cognome;
        }

        public String getEmail() {
            return email;
        }

        public  boolean isManutentore () { return this.manutentore; }
}
