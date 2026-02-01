package pl.edu.notes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllNotes() throws Exception {
        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createNote() throws Exception {
        String json = "{\"title\":\"Testowa\",\"content\":\"Tresc testowa\"}";

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Testowa"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void getNonExistingNote() throws Exception {
        mockMvc.perform(get("/api/notes/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNote() throws Exception {
        String json = "{\"title\":\"Do usuniecia\",\"content\":\"test\"}";

        String response = mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = response.split("\"id\":")[1].split(",")[0];

        mockMvc.perform(delete("/api/notes/" + id))
                .andExpect(status().isNoContent());
    }
}
