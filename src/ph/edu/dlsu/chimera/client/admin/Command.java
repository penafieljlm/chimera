package ph.edu.dlsu.chimera.client.admin;

import java.util.HashMap;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An instance of this class constitutes an object which can translate a command string and a set of parameters to a ServerMessage object.
 * @author John Lawrence M. Penafiel
 */
public class Command {

    /**
     * The regular expression that a parameter of a message command must match.
     */
    public static String PARAMETER_REGEX = "[#]([^%#=]|[ ])+[=]([^%#=]|[ ])+[#]";

    /**
     * The command of the message.
     */
    private String command;

    /**
     * The parameters of the command.
     */
    private final HashMap<String, String> parameters;

    /**
     * Constructs a new Command object.
     * @param subject - the string which to base the new Command object on.
     * @throws Exception
     */
    public Command(String subject) throws Exception {
        this();
        //create temp
        StringBuilder subj = new StringBuilder(subject);
        //clean spaces
        Matcher paramMatches = Pattern.compile(Command.PARAMETER_REGEX).matcher(subj);
        while(paramMatches.find()) {
            String match = paramMatches.group();
            match = match.replace(" ", "%");
            subj.delete(paramMatches.start(), paramMatches.end());
            subj.insert(paramMatches.start(), match);
        }
        //parse
        String[] components = subj.toString().split(" ");
        if (components.length == 0) {
            throw new Exception("The message command is zero in length!");
        }
        this.command = components[0];
        for (int i = 1; i < components.length; i++) {
            components[i] = components[i].replace("%", " ");
            if (components[i].matches(Command.PARAMETER_REGEX)) {
                String[] param_comps = components[i].replace("#", "").split("=");
                String key = param_comps[0];
                String value = param_comps[1];
                if (this.parameters.containsKey(key)) {
                    throw new Exception("Duplicate parameter: " + components[i] + " at index: " + i + "!");
                } else {
                    this.parameters.put(key, value);
                }
            } else {
                this.command = null;
                this.parameters.clear();
                throw new Exception("Invalid parameter: " + components[i] + " at index: " + i + "!");
            }
        }
    }

    /**
     * Constructs a new Command object.
     */
    public Command() {
        this.command = null;
        this.parameters = new HashMap<String, String>();
    }


    /**
     * Adds a new parameter or replaces an existing one.
     * @param key - the parameter label.
     * @param value - the value of the parameter.
     */
    public void putParameter(String key, String value) {
        this.parameters.put(key, value);
    }

    /**
     * Gets a parameter value.
     * @param key - the label of the parameter value to be retrieved.
     * @return the value of the parameter of the specified label.
     */
    public String getParameterValue(String key) {
        return this.parameters.get(key);
    }

    /**
     * @return the command string of this ServerMessage object.
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * @return the set of keys (parameter names) associated with this ServerMessage object.
     */
    public Set<String> getKeys() {
        return this.parameters.keySet();
    }
    
}