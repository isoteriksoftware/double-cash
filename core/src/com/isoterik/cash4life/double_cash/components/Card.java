package com.isoterik.cash4life.double_cash.components;

import com.isoterik.mgdx.Component;

public class Card extends Component {
    public final int number;

    public Card(String regionName) {
        if (regionName.startsWith("shirt"))
            this.number = 0;
        else
            this.number = Integer.parseInt(regionName.replaceAll("[^0-9]", ""));
    }
}




























