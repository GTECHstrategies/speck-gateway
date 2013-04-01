package org.bodytrack.airbot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * <code>CsvDataSampleStore</code> handles storage {@link AirBot.DataSample data samples}, storing them in a text file
 * as records with comma-delimited values.
 * Does not support retrieval.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class CsvDataSampleStore implements DataSampleStore
   {
   private static final Logger LOG = Logger.getLogger(CsvDataSampleStore.class);

   @NotNull
   private BufferedWriter writer;

   CsvDataSampleStore(@NotNull final AirBotConfig airBotConfig)
      {
      final File dataFileDirectory = AirBotUploaderConstants.FilePaths.getDeviceDataDirectory(airBotConfig);
      final File dataFile = new File(dataFileDirectory, "data_samples.csv");
      final boolean doesFileAlreadyExist = dataFile.exists();
      try
         {
         writer = new BufferedWriter(new FileWriter(dataFile, true));
         if (!doesFileAlreadyExist)
            {
            write("sample_timestamp_utc_secs,raw_particle_count,particle_count,temperature,humidity,download_timestamp_utc_millis");
            }
         }
      catch (FileNotFoundException e)
         {
         LOG.error("CsvDataSampleStore.CsvDataSampleStore(): FileNotFoundException while trying to create the BufferedWriter", e);
         System.exit(1);
         }
      catch (IOException e)
         {
         LOG.error("CsvDataSampleStore.CsvDataSampleStore(): IOException while trying to create the BufferedWriter", e);
         System.exit(1);
         }
      }

   @Override
   @NotNull
   public SaveResult save(@NotNull final AirBot.DataSample dataSample)
      {
      LOG.debug("CsvDataSampleStore.save(): saving sample " + dataSample.getSampleTime());
      try
         {
         write(dataSample.toCsv());
         }
      catch (IOException e)
         {
         LOG.error("CsvDataSampleStore.save(): IOException while trying to write to the file", e);
         return SaveResult.FAILURE_ERROR;
         }

      return SaveResult.SUCCESS;
      }

   private void write(final String str) throws IOException
      {
      writer.write(str);
      writer.newLine();
      writer.flush();
      }

   /** Not supported, does nothing. */
   @Override
   public void resetStateOfUploadingSamples()
      {
      // not supported
      }

   /** Not supported, so the returned {@link DataSampleSet} will never contain any data samples. */
   @NotNull
   @Override
   public DataSampleSet getDataSamplesToUpload(final int maxNumberRequested)
      {
      return new DataSampleSetImpl(null);
      }

   /** Not supported, does nothing. */
   @Override
   public void markDataSamplesAsUploaded(@NotNull final DataSampleSet dataSampleSet)
      {
      // not supported
      }

   /** Not supported, does nothing. */
   @Override
   public void markDataSamplesAsFailed(@NotNull final DataSampleSet dataSampleSet)
      {
      // not supported
      }

   @Override
   public void shutdown()
      {
      try
         {
         writer.close();
         LOG.debug("CsvDataSampleStore.shutdown(): Successfully shut down the CsvDataSampleStore");
         }
      catch (IOException e)
         {
         LOG.error("CsvDataSampleStore.shutdown(): IOException while trying to close the writer", e);
         }
      }
   }
