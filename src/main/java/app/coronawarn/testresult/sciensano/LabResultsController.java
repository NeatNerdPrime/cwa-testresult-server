/*
 * Coronalert / cwa-testresult-server
 *
 * (c) 2020 Devside SRL
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package app.coronawarn.testresult.sciensano;

import app.coronawarn.testresult.entity.TestResultEntity;
import static app.coronawarn.testresult.entity.TestResultEntity.pendingResult;
import app.coronawarn.testresult.model.MobileTestResultList;
import app.coronawarn.testresult.model.MobileTestResultUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@Profile("allow-test-result-insert")
public class LabResultsController {

  private final TestResultRepository testResultRepository;

  /**
   * Insert or update test results.
   *
   * @param list the test result list request
   * @return the response
   */
  @Operation(description = "Create test results from collection.")
  @PostMapping(value = "/v1/lab/results", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<?> results(@RequestBody @NotNull @Valid MobileTestResultList list) {
    log.info("Received {} test results to insert or update from lab.", list.getMobileTestResultUpdateRequest().size());
    list.getMobileTestResultUpdateRequest().forEach(this::createOrUpdate);
    return ResponseEntity.noContent().build();
  }

  /**
   * This is a helper method used for our internal REST call to insert test methods.
   * We either update an existing test result or create a new one.
   *
   * @param mobileTestResultUpdateRequest the MobileTestResultRequest containing the
   *                                      mobileTestId and datePatientInfectious.
   */
  private void createOrUpdate(@Valid MobileTestResultUpdateRequest mobileTestResultUpdateRequest) {
    TestResultEntity testResultEntity = testResultRepository.findByMobileTestIdAndDatePatientInfectious(
      mobileTestResultUpdateRequest.getMobileTestId(),
      mobileTestResultUpdateRequest.getDatePatientInfectious()
    ).orElse(pendingResult(
      mobileTestResultUpdateRequest.getMobileTestId(),
      mobileTestResultUpdateRequest.getDatePatientInfectious())
    );

    testResultEntity.setDateTestPerformed(mobileTestResultUpdateRequest.getDateTestPerformed());
    testResultEntity.setDateSampleCollected(mobileTestResultUpdateRequest.getDateSampleCollected());
    testResultEntity.setResultChannel(mobileTestResultUpdateRequest.getResultChannel());
    testResultEntity.setResult(mobileTestResultUpdateRequest.getResult());

    testResultRepository.save(testResultEntity);

  }
}
