package jlab.desserts.Activity.Utils;

/*
 * Created by Javier on 21/03/2020.
 */

public class Dessert {

    private int id;
    private String name;
    private String description;
    private String ingredients;
    private String prepDescription;
    private int countIngredients;
    private int difficulty;
    private int dessertId;
    private boolean isFavorite;

    public Dessert(int id, String name, String description, String ingredients,
                   String prepDescription, int difficulty, int dessertId, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.prepDescription = prepDescription;
        this.difficulty = difficulty;
        this.dessertId = dessertId;
        this.isFavorite = isFavorite;
        this.countIngredients = getCountIngredients(ingredients);
    }

    public int getCountIngredients (String ingredients) {
        int count = 0;
        for (int i = 0; i < ingredients.length(); i++)
            if (ingredients.charAt(i) == '•')
                count++;
        return count;
    }

    public int getId() {
        return id;
    }

    public int getDessertId() {
        return dessertId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getPrepDescription() {
        return prepDescription;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getCountIngredients() {
        return countIngredients;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
