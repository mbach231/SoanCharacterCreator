

package soancharactercreator;

import soancharactercreator.SkillLoader.EntryType;

/**
 *
 * 
 */
public class BaseSkill {

    private final String name_;
    private final int cost_;
    private final EntryType type_;
    
    public BaseSkill(String name, int cost, EntryType type) {
        name_ = name;
        cost_ = cost;
        type_ = type;
    }
    
    public String getName() {
        return name_;
    }
    
    public int getCost() {
        return cost_;
    }
    
    public EntryType getType() {
        return type_;
    }
    
}
