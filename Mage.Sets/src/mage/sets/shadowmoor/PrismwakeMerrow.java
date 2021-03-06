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
package mage.sets.shadowmoor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import mage.MageInt;
import mage.ObjectColor;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.continious.SetCardColorTargetEffect;
import mage.abilities.keyword.FlashAbility;
import mage.cards.CardImpl;
import mage.choices.ChoiceColor;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.TargetPermanent;
import mage.target.targetpointer.FixedTarget;

/**
 *
 * @author jeffwadsworth
 */
public class PrismwakeMerrow extends CardImpl {

    public PrismwakeMerrow(UUID ownerId) {
        super(ownerId, 46, "Prismwake Merrow", Rarity.COMMON, new CardType[]{CardType.CREATURE}, "{2}{U}");
        this.expansionSetCode = "SHM";
        this.subtype.add("Merfolk");
        this.subtype.add("Wizard");

        this.color.setBlue(true);
        this.power = new MageInt(2);
        this.toughness = new MageInt(1);

        // Flash
        this.addAbility(FlashAbility.getInstance());

        // When Prismwake Merrow enters the battlefield, target permanent becomes the color or colors of your choice until end of turn.
        Ability ability = new EntersBattlefieldTriggeredAbility(new ChangeColorOrColorsTargetEffect(), false);
        ability.addTarget(new TargetPermanent());
        this.addAbility(ability);

    }

    public PrismwakeMerrow(final PrismwakeMerrow card) {
        super(card);
    }

    @Override
    public PrismwakeMerrow copy() {
        return new PrismwakeMerrow(this);
    }
}

class ChangeColorOrColorsTargetEffect extends OneShotEffect {

    public ChangeColorOrColorsTargetEffect() {
        super(Outcome.Neutral);
        staticText = "target permanent becomes the color or colors of your choice until end of turn";
    }

    public ChangeColorOrColorsTargetEffect(final ChangeColorOrColorsTargetEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player you = game.getPlayer(source.getControllerId());
        Permanent target = game.getPermanent(source.getFirstTarget());
        List<ObjectColor> chosenColors = new ArrayList<>();
        if (you != null && target != null) {
            for (int i = 0; i < 5; i++) {
                if (!you.chooseUse(Outcome.Neutral, "Do you wish to choose another color?", game)) {
                    break;
                }
                ChoiceColor choiceColor = new ChoiceColor();
                you.choose(Outcome.Benefit, choiceColor, game);
                if (!you.isInGame()) {
                    return false;
                }
                game.informPlayers(target.getName() + ": " + you.getName() + " has chosen " + choiceColor.getChoice());
                if (choiceColor.getColor().isBlack()) {
                    chosenColors.add(ObjectColor.BLACK);
                } else if (choiceColor.getColor().isBlue()) {
                    chosenColors.add(ObjectColor.BLUE);
                } else if (choiceColor.getColor().isRed()) {
                    chosenColors.add(ObjectColor.RED);
                } else if (choiceColor.getColor().isGreen()) {
                    chosenColors.add(ObjectColor.GREEN);
                } else if (choiceColor.getColor().isWhite()) {
                    chosenColors.add(ObjectColor.WHITE);
                }
            }
            for (ObjectColor color : chosenColors) {
                ContinuousEffect effect = new SetCardColorTargetEffect(color, Duration.EndOfTurn, "is " + color);
                effect.setTargetPointer(new FixedTarget(source.getFirstTarget()));
                game.addEffect(effect, source);
            }
            return true;
        }
        return false;
    }

    @Override
    public ChangeColorOrColorsTargetEffect copy() {
        return new ChangeColorOrColorsTargetEffect(this);
    }
}
