/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package soancharactercreator.requirements;

import soancharactercreator.PlayerCharacter;
import soancharactercreator.Skill;

/**
 *
 * 
 */
public abstract class Requirement {

    protected Requirement() {}
    
    abstract public boolean meetsRequirements(PlayerCharacter character, Skill skill);
}
