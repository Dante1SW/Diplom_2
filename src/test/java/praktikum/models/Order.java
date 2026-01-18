package praktikum.models;

import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("ingredients")
    private String[] ingredients;

    // Конструктор с ингредиентами
    public Order(String[] ingredients) {
        this.ingredients = ingredients;
    }

    // Пустой конструктор
    public Order() {
        this.ingredients = new String[0];
    }

    // Геттер для ингредиентов
    public String[] getIngredients() {
        return ingredients;
    }

    // Сеттер для ингредиентов
    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }


}