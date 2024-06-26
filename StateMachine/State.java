package StateMachine;

public enum  State {
        START,      // Initial state
        PRONOUN,    // State for processing a pronoun token
        ARTICLE,    // State for article
        VERB,       // State for processing a verb token
        ADJECTIVE,   // State for processing an adjective token
        NOUN,   // State for processing a noun
        ADVERB,
        DOT,
        CONJ,
        END,
        COMMA,
        NAN;   // State for processing a noun

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }

        public static State fromString(String text) {
            for (State state : State.values()) {
                if (state.toString().equalsIgnoreCase(text)) {
                    return state;
                }
            }
            return NAN;
        }

        public State next() {
            State[] colors = State.values();
            return colors[(this.ordinal()+1)%colors.length];
          }
        

        static public State first() {
            return State.values()[0];
        }
}
