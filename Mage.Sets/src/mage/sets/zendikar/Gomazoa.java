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
package mage.sets.zendikar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.DefenderAbility;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.WatcherScope;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.watchers.Watcher;

/**
 *
 * @author jeffwadsworth
 */
public class Gomazoa extends CardImpl {

    public Gomazoa(UUID ownerId) {
        super(ownerId, 46, "Gomazoa", Rarity.UNCOMMON, new CardType[]{CardType.CREATURE}, "{2}{U}");
        this.expansionSetCode = "ZEN";
        this.subtype.add("Jellyfish");

        this.color.setBlue(true);
        this.power = new MageInt(0);
        this.toughness = new MageInt(3);

        // Defender
        this.addAbility(DefenderAbility.getInstance());

        // Flying
        this.addAbility(FlyingAbility.getInstance());

        // {tap}: Put Gomazoa and each creature it's blocking on top of their owners' libraries, then those players shuffle their libraries.
        this.addAbility(new SimpleActivatedAbility(Zone.BATTLEFIELD, new GomazoaEffect(), new TapSourceCost()));
        this.addWatcher(new BlockedByWatcher());

    }

    public Gomazoa(final Gomazoa card) {
        super(card);
    }

    @Override
    public Gomazoa copy() {
        return new Gomazoa(this);
    }
}

class GomazoaEffect extends OneShotEffect {

    public GomazoaEffect() {
        super(Outcome.Neutral);
        this.staticText = "Put {this} and each creature it's blocking on top of their owners' libraries, then those players shuffle their libraries";
    }

    public GomazoaEffect(final GomazoaEffect effect) {
        super(effect);
    }

    @Override
    public GomazoaEffect copy() {
        return new GomazoaEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            List<UUID> creaturesBlocked;
            List<UUID> players = new ArrayList<>();
            Permanent gomazoa = game.getPermanent(source.getSourceId());
            if (gomazoa != null) {
                controller.moveCardToLibraryWithInfo(gomazoa, source.getSourceId(), game, Zone.BATTLEFIELD, true, true);
                players.add(gomazoa.getOwnerId());
            }

            BlockedByWatcher watcher = (BlockedByWatcher) game.getState().getWatchers().get("BlockedByWatcher", source.getSourceId());
            creaturesBlocked = watcher.blockedByWatcher;

            for (UUID blockedById : creaturesBlocked) {
                Permanent blockedByGomazoa = game.getPermanent(blockedById);
                if (blockedByGomazoa != null && blockedByGomazoa.isAttacking()) {
                    players.add(blockedByGomazoa.getOwnerId());
                    Player owner = game.getPlayer(blockedByGomazoa.getOwnerId());
                    if (owner != null) {
                        owner.moveCardToLibraryWithInfo(blockedByGomazoa, source.getSourceId(), game, Zone.BATTLEFIELD, true, true);
                    }
                }
            }
            for (UUID player : players) {
                Player owner = game.getPlayer(player);
                if (owner != null) {
                    owner.shuffleLibrary(game);
                }
            }
            return true;
        }
        return false;

    }
}

class BlockedByWatcher extends Watcher {

    public List<UUID> blockedByWatcher = new ArrayList<>();

    public BlockedByWatcher() {
        super("BlockedByWatcher", WatcherScope.CARD);
    }

    public BlockedByWatcher(final BlockedByWatcher watcher) {
        super(watcher);
        this.blockedByWatcher.addAll(watcher.blockedByWatcher);
    }

    @Override
    public BlockedByWatcher copy() {
        return new BlockedByWatcher(this);
    }

    @Override
    public void watch(GameEvent event, Game game) {
        if (event.getType() == GameEvent.EventType.BLOCKER_DECLARED) {
            if (sourceId.equals(event.getSourceId()) && !blockedByWatcher.contains(event.getTargetId())) {
                blockedByWatcher.add(event.getTargetId());
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        blockedByWatcher.clear();
    }
}
