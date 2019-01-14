package urlshortener.team.domain;

import java.util.List;

public class ResponseStadistics {


    private Long numberOfClicks;
    private List<Stadistics> location;

    public ResponseStadistics(Long numberOfClicks, List<Stadistics>  location) {
        this.numberOfClicks = numberOfClicks;
        this.location = location;
    }

    public Long getNumberOfClicks() {
        return numberOfClicks;
    }

    public void setNumberOfClicks(Long numberOfClicks) {
        this.numberOfClicks = numberOfClicks;
    }

    public List<Stadistics> getLocation() {
        return location;
    }

    public void setLocation(List<Stadistics> location) {
        this.location = location;
    }
}
