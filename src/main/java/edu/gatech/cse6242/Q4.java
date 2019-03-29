package edu.gatech.cse6242;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.IOException;

public class Q4 {

  public static void main(String[] args) throws Exception {
    Configuration conf1 = new Configuration();
    Job job1 = Job.getInstance(conf1, "Q41");
    String hkurmi = args[1]+"-hkurmi";

   
    job1.setJarByClass(Q4.class);
   
    job1.setMapperClass(firstMapper.class);
    job1.setReducerClass(totalReducer.class);
    job1.setOutputKeyClass(IntWritable.class);
    job1.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job1, new Path(args[0]));
    FileOutputFormat.setOutputPath(job1, new Path(hkurmi));
	job1.waitForCompletion(true);
	
    Configuration conf2 = new Configuration();
    Job job = Job.getInstance(conf2, "Q42");
    job.setJarByClass(Q4.class);
    job.setMapperClass(secondMapper.class);
    job.setReducerClass(totalReducer.class);
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(hkurmi));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    
    System.exit(job.waitForCompletion(true)?0:1);
}

  static class firstMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable>{
  		@Override
  		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
  					
					String str = value.toString();
                    if(str.length() > 1){
						String[] data = str.split("\t");
						IntWritable src = new IntWritable(Integer.parseInt(data[0]));
						IntWritable trgt = new IntWritable(Integer.parseInt(data[1]));
						IntWritable outDegree = new IntWritable(1);
						IntWritable inDegree = new IntWritable(-1);
						context.write(src, outDegree);
						context.write(trgt, inDegree);
          	       }
  				}
  			}

  static class secondMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable>{
  		@Override
  		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{

  					String str = value.toString();
                    if(str.length()>1){
						String[] data = str.split("\t");
						IntWritable diff = new IntWritable(Integer.parseInt(data[1]));
						IntWritable posOne = new IntWritable(1);
						context.write(diff, posOne);
  				   }
          	}
  			}

  static class totalReducer extends Reducer<IntWritable,IntWritable,IntWritable,IntWritable>{
  		@Override
  		public void reduce(IntWritable key, Iterable<IntWritable> values, Context context)throws IOException, InterruptedException{
  					int totalNodes = 0;
  					for(IntWritable value: values)
  						totalNodes += value.get();
  					context.write(key, new IntWritable(totalNodes));
  				}
  		}
}