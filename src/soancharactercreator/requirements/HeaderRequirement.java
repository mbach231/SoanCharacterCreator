/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package soancharactercreator.requirements;

import soancharactercreator.Header;
import soancharactercreator.PlayerCharacter;
import soancharactercreator.Skill;

/**
 *
 * 
 */
public class HeaderRequirement extends Requirement {

    private final Header header_;
    
    public HeaderRequirement(Header header) {
        header_ = header;
    }
    
    public Header getHeader() {
        return header_;
    }

    @Override
    public boolean meetsRequirements(PlayerCharacter character, Skill skill) {
        return character.hasSkill(header_);
    }
    
}
