package com.barbariania.awsinfo;

import com.amazonaws.util.EC2MetadataUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTest {
  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @BeforeEach
  void setup() {
    this.mockMvc = webAppContextSetup(webApplicationContext).build();
  }

  @Test
  void getInstanceInfo() throws Exception {
    final EC2MetadataUtils.InstanceInfo instanceInfoMock = mock(EC2MetadataUtils.InstanceInfo.class);
    when(instanceInfoMock.getAvailabilityZone()).thenReturn("eu-central-1b");
    final String responseString = performGetRoot(instanceInfoMock, status().isOk());

    final EC2MetadataUtils.InstanceInfo response = OBJECT_MAPPER.readValue(responseString, EC2MetadataUtils.InstanceInfo.class);
    assertThat(response.getAvailabilityZone()).isEqualTo("eu-central-1b");
  }

  @Test
  void getInstanceInfo_fail() throws Exception {
    final String contentAsString = performGetRoot(null, status().isInternalServerError());
    assertThat(contentAsString).isEqualTo("AWS returned no instanceInfo, check machine logs");
  }

  private String performGetRoot(EC2MetadataUtils.InstanceInfo instanceInfoMock, ResultMatcher resultMatcher) throws Exception {
    try (MockedStatic<EC2MetadataUtils> ec2UtilsMock = Mockito.mockStatic(EC2MetadataUtils.class)) {

      ec2UtilsMock.when(EC2MetadataUtils::getInstanceInfo)
          .thenReturn(instanceInfoMock);

      final MvcResult mvcResult = mockMvc.perform(get("/"))
          .andExpect(resultMatcher)
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andReturn();

      ec2UtilsMock.verify(EC2MetadataUtils::getInstanceInfo);

      return mvcResult.getResponse().getContentAsString();
    }
  }
}
