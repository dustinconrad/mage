/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
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
package mage.abilities.costs;

import java.util.Iterator;
import mage.abilities.Ability;
import mage.abilities.SpellAbility;
import mage.abilities.StaticAbility;
import mage.abilities.condition.Condition;
import mage.abilities.costs.mana.ManaCost;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.game.Game;
import mage.players.Player;

/**
 *
 * @author LevelX2
 */
public class AlternativeCostSourceAbility extends StaticAbility implements AlternativeSourceCosts {

    Costs<AlternativeCost2> alternateCosts = new CostsImpl<>();
    protected Condition condition;
    protected String rule;

    public AlternativeCostSourceAbility(Cost cost) {
        this(cost, null);
    }

    public AlternativeCostSourceAbility(Condition conditon) {
        this(null, conditon, null);
    }

    public AlternativeCostSourceAbility(Cost cost, Condition conditon) {
        this(cost, conditon, null);
    }

    public AlternativeCostSourceAbility(Cost cost, Condition condition, String rule) {
        super(Zone.ALL, null);
        this.convertToAlternativeCostAndAdd(cost);
        this.setRuleAtTheTop(true);
        this.condition = condition;
        this.rule = rule;
    }

    @Override
    public void addCost(Cost cost) {
        this.convertToAlternativeCostAndAdd(cost);
    }

    private void convertToAlternativeCostAndAdd(Cost cost) {
        if (cost != null) {
            AlternativeCost2 alternativeCost = new AlternativeCost2Impl(null, null, cost);
            this.alternateCosts.add(alternativeCost);
        }
    }

    public AlternativeCostSourceAbility(final AlternativeCostSourceAbility ability) {
        super(ability);
        this.alternateCosts = ability.alternateCosts;
        this.condition = ability.condition;
        this.rule = ability.rule;
    }

    @Override
    public AlternativeCostSourceAbility copy() {
        return new AlternativeCostSourceAbility(this);
    }

    @Override
    public boolean isAvailable(Ability source, Game game) {
        if (condition != null) {
            return condition.apply(game, source);
        }
        return true;
    }

    @Override
    public boolean askToActivateAlternativeCosts(Ability ability, Game game) {
        if (ability instanceof SpellAbility) {
            Player player = game.getPlayer(ability.getControllerId());
            if (player != null) {
                if (alternateCosts.canPay(ability.getSourceId(), ability.getControllerId(), game) &&
                        player.chooseUse(Outcome.Detriment, alternateCosts.isEmpty() ? "Cast without paying its mana cost?":"Pay alternative costs? (" + alternateCosts.getText() +")", game)) {
                    ability.getManaCostsToPay().clear();
                    ability.getCosts().clear();
                    for (Cost cost : alternateCosts) {
                        AlternativeCost2 alternateCost = (AlternativeCost2) cost;
                        alternateCost.activate();
                        for (Iterator it = ((Costs) alternateCost).iterator(); it.hasNext();) {
                            Cost costDeailed = (Cost) it.next();
                            if (costDeailed instanceof ManaCost) {
                                ability.getManaCostsToPay().add((ManaCost) costDeailed.copy());
                            } else {
                                ability.getCosts().add(costDeailed.copy());
                            }
                        }
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return isActivated(ability, game);
    }

    @Override
    public boolean isActivated(Ability source, Game game) {
        for (AlternativeCost2 cost : alternateCosts) {
            if (cost.isActivated(game)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getCastMessageSuffix(Game game) {
        return alternateCosts.isEmpty() ? " without paying it's mana costs":" using alternative casting costs";
    }

    @Override
    public String getRule() {
        if (rule != null) {
            return rule;
        }
        // you may cast Massacre without paying its mana cost.
        StringBuilder sb = new StringBuilder();
        if (condition != null) {
            sb.append(condition.toString());
            if (alternateCosts.size() > 1) {
                sb.append(", rather than pay {source}'s mana cost, ");
            } else {
                sb.append(", you may ");
            }
        } else {
            sb.append("You may ");
        }
        int numberCosts = 0;
        String remarkText = "";
        for (AlternativeCost2 alternativeCost : alternateCosts) {            
            if (numberCosts == 0) {
                if (alternativeCost.getCost() instanceof ManaCost) {
                    sb.append("pay ");
                }
                sb.append(alternativeCost.getText(false));
                remarkText = alternativeCost.getReminderText();
            } else {
                sb.append(" and ");
                if (alternativeCost.getCost() instanceof ManaCost) {
                    sb.append("pay ");
                }
                sb.append(alternativeCost.getText(true));
            }
            ++numberCosts;
        }
        if (condition == null || alternateCosts.size() == 1) {
            sb.append(" rather than pay {source}'s mana cost");
        } else if (alternateCosts.isEmpty()) {
            sb.append("cast {this} without paying its mana cost");
        }
        sb.append(".");
        if (numberCosts == 1 && remarkText != null) {
            sb.append(" ").append(remarkText);
        }
        return sb.toString();
    }

    @Override
    public Costs<Cost> getCosts() {
        Costs<Cost> alterCosts = new CostsImpl<>();
        alterCosts.addAll(alternateCosts);
        return alterCosts;
    }
    
}
