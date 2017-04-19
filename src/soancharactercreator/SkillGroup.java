

package soancharactercreator;

import java.util.List;
import java.util.Set;

/**
 *
 * 
 */
public class SkillGroup {

    private final String name_;
    private final Set<String> skillNames_;
    
    public SkillGroup(String name, Set<String> skills) {
        name_ = name;
        skillNames_ = skills;
    }
    
    public String getName() {
        return name_;
    }
    
    public Set<String> getSkills() {
        return skillNames_;
    }
    
}
