/*
 *  Copyright 2011 BetaSteward_at_googlemail.com. All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 * 
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 * 
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 * 
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.sets.scarsofmirrodin;

import java.util.UUID;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.condition.common.IsStepCondition;
import mage.abilities.costs.common.MetalcraftCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.decorator.ConditionalActivatedAbility;
import mage.abilities.effects.common.ReturnSourceFromGraveyardToBattlefieldEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.abilities.keyword.HasteAbility;
import mage.cards.CardImpl;
import mage.constants.PhaseStep;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class KuldothaPhoenix extends CardImpl {

    public KuldothaPhoenix(UUID ownerId) {
        super(ownerId, 95, "Kuldotha Phoenix", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{2}{R}{R}{R}");
        this.expansionSetCode = "SOM";
        this.subtype.add("Phoenix");
        this.color.setRed(true);
        this.power = new MageInt(4);
        this.toughness = new MageInt(4);

        // Flying, haste
        
        this.addAbility(FlyingAbility.getInstance());
        this.addAbility(HasteAbility.getInstance());
        // Metalcraft - : Return Kuldotha Phoenix from your graveyard to the battlefield. 
        // Activate this ability only during your upkeep and only if you control three or more artifacts.        
        Ability ability = new ConditionalActivatedAbility(Zone.GRAVEYARD, 
                new ReturnSourceFromGraveyardToBattlefieldEffect(true), 
                new ManaCostsImpl("{4}"), 
                new IsStepCondition(PhaseStep.UPKEEP),
                null
        );        
        ability.addCost(new MetalcraftCost());       
        this.addAbility(ability);
    }

    public KuldothaPhoenix(final KuldothaPhoenix card) {
        super(card);
    }

    @Override
    public KuldothaPhoenix copy() {
        return new KuldothaPhoenix(this);
    }

}