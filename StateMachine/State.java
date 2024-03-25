package StateMachine;

public enum  State {
        START,      // Initial state
        PRONOUN,    // State for processing a pronoun token
        ARTICLE,    // State for article
        VERB,       // State for processing a verb token
        ADJECTIVE,   // State for processing an adjective token
        NOUN,   // State for processing a noun
        DOT,
        END,
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
}
