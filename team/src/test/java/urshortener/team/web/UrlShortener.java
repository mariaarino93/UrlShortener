package urshortener.team.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.team.domain.Link;
import urlshortener.team.repository.ClickRepository;
import urlshortener.team.repository.LinkRepository;
import urlshortener.team.web.UrlShortenerController;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

public class UrlShortener {

    private MockMvc mockMvc;

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private ClickRepository clickRespository;

    @InjectMocks
    private UrlShortenerController urlShortener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build();
    }

    @Test
    public void thatRedirectIfKeyExists() {
        try {
            Link link = linkRepository.findByKey("example");
            assertEquals(link,MockUrlShortener.exampleUrl());
            this.mockMvc.perform(get("/{id}", "example")).andDo(print()).andExpect(status().isOk())
                    .andExpect(redirectedUrl("http://example.com/"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void thatRedirectIfKeyNotExists() {
        try {
            when(linkRepository.findByKey("example")).thenReturn(null);

            this.mockMvc.perform(get("/{id}", "example"))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void thatShortenerCreatesARedirectIfTheURLisOK() {
        try {
            mockMvc.perform(post("/link").param("originalUrl", "http://google.com/"))
                    .andDo(print())
                    .andExpect(redirectedUrl("http://localhost/f684a3c4"))
                    .andExpect(jsonPath("shortUrl", is("http://localhost:8080/f684a3c4")))
                    .andExpect(jsonPath("originalUrl", is("http://google.com/")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void thatCreatesAndRedirectWithCustomURL() {

        try {
            mockMvc.perform(post("/link").param("originalUrl", "http://google.com/").param(
                            "customUrl", "google")).andDo(print())
                    .andExpect(redirectedUrl("http://localhost/f684a3c4"))
                    .andExpect(status().isCreated())
                    .andExpect((ResultMatcher) jsonPath("shortUrl", is("http://localhost:8080/f684a3c4")))
                    .andExpect((ResultMatcher) jsonPath("originalUrl", is("http://google.com/")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void thatShortenerFailsIfTheURLisWrong() throws Exception {

        mockMvc.perform(post("/link").param("customUrl", "example")).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void thatShortenerFailsIfTheRepositoryReturnsNull() throws Exception {
        when(linkRepository.save(any(Link.class)))
                .thenReturn(null);

        mockMvc.perform(post("/link").param("url", "example")).andDo(print())
                .andExpect(status().isNotFound());
    }

}
