package nk.bluefrog.rythusevaoffice.activities.search;

public class SearchItems implements Comparable {

    SearchModel searchModel;
    int percentage;

    public SearchItems(int percentage, SearchModel searchModel) {
        this.percentage = percentage;
        this.searchModel = searchModel;
    }

    public SearchModel getSearchResult() {
        return searchModel;
    }

    public void setSearchResult(SearchModel searchModel) {
        this.searchModel = searchModel;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }


   /* @Override
    public String toString() {
        return "[ percentage=" + percentage + ", SearchResult Title=" + searchResult.getTitle() + "]";
    }
*/
    @Override
    public int compareTo(Object o) {
        int compareage = ((SearchItems) o).getPercentage();
        /* For Ascending order*/
       // return this.percentage - compareage;

        /* For Descending order do like this */
        return compareage-this.percentage;
    }
}
