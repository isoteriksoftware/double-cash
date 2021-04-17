package com.isoterik.cash4life.double_cash.components;

import com.isoterik.mgdx.Component;

public class Card extends Component {
    public final int number;

    public Card(String regionName) {
        this.number = Integer.parseInt(regionName.replaceAll("[^0-9]", ""));
    }
}




























