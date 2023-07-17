package com.wangyan.ncbi.services.impl;

import com.wangyan.ncbi.pojo.PubMed;
import com.wangyan.ncbi.services.INLPService;
import com.wangyan.ncbi.services.IPubMedService;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;

import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;

import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.text.sentenceiterator.*;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NLPServiceImpl implements INLPService {

    @Autowired
    IPubMedService pubMedService;

    public void MNIST(){
//        try {
//            int numInputs = 784;
//            int numOutputs = 10;
//            int batchSize = 64;
//            int rngSeed = 123;
//
//            // 加载MNIST数据集
//            MnistDataSetIterator mnistTrain = new MnistDataSetIterator(batchSize, true, rngSeed);
//            MnistDataSetIterator mnistTest = new MnistDataSetIterator(batchSize, false, rngSeed);
//
//            // 构建神经网络模型
//            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
//                    .seed(rngSeed)
//                    .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
//                    .list()
//                    .layer(0, new DenseLayer.Builder()
//                            .nIn(numInputs)
//                            .nOut(100)
//                            .activation(Activation.valueOf("relu"))
//                            .build())
//                    .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
//                            .nIn(100)
//                            .nOut(numOutputs)
//                            .activation(Activation.valueOf("softmax"))
//                            .build())
//                    .backprop(true)
//                    .pretrain(false)
//                    .build();
//
//            MultiLayerNetwork model = new MultiLayerNetwork(conf);
//            model.init();
//
//            // 训练模型
//            for (int i = 0; i < 10; i++) {
//                while (mnistTrain.hasNext()) {
//                    DataSet next = mnistTrain.next();
//                    model.fit(next);
//                }
//                mnistTrain.reset();
//                Evaluation evaluation = model.evaluate(mnistTest);
//                System.out.println("Epoch " + i + " Accuracy: " + evaluation.accuracy());
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
    @Override
    public void  trainWord2VecModel2(){
        try {
            //            String filePath = new ClassPathResource("raw_sentences.txt").getFile().getAbsolutePath();

            // 加载数据
            log.info("Load data....");
            SentenceIterator iter = new LineSentenceIterator(new File("/home/wangyang/workspace/ncbi-genome-download/raw_sentences.txt"));
            iter.setPreProcessor(new SentencePreProcessor() {
                @Override
                public String preProcess(String sentence) {
                    return sentence.toLowerCase();
                }
            });

            // 数据分词
            // 在每行中按空格分词
            TokenizerFactory t = new DefaultTokenizerFactory();
            t.setTokenPreProcessor(new CommonPreprocessor());
            log.info("Building model....");
            /**
             * batchSize是每次处理的词的数量。
             * minWordFrequency是一个词在语料中必须出现的最少次数。本例中出现不到五次的词都不予学习。词语必须在多种上下文中出现，才能让模型学习到有用的特征。对于规模很大的语料库，理应提高出现次数的下限。
             * useAdaGrad－AdaGrad为每个特征生成一个不同的梯度。在此处不需要考虑。
             * layerSize指定词向量中的特征数量，与特征空间的维度数量相等。以500个特征值表示的词会成为一个500维空间中的点。
             * iterations是网络在处理一批数据时允许更新系数的次数。迭代次数太少，网络可能来不及学习所有能学到的信息；迭代次数太多则会导致网络定型时间变长。
             * learningRate是每一次更新系数并调整词在特征空间中的位置时的步幅。
             * minLearningRate是学习速率的下限。学习速率会随着定型词数的减少而衰减。如果学习速率下降过多，网络学习将会缺乏效率。这会使系数不断变化。
             * iterate告知网络当前定型的是哪一批数据集。
             * tokenizer将当前一批的词输入网络。
             * vec.fit()让已配置好的网络开始定型。
             * */
            Word2Vec vec = new Word2Vec.Builder()
                    .minWordFrequency(5)
                    .iterations(1)
                    .layerSize(100)
                    .seed(42)
                    .windowSize(5)
                    .iterate(iter)
                    .tokenizerFactory(t)
                    .build();

            log.info("Fitting Word2Vec model....");
            vec.fit();
            // 下一步是评估特征向量的质量
            // 编写词向量
            WordVectorSerializer.writeWordVectors(vec, "pathToWriteto.txt");

            log.info("Closest Words:");
            Collection<String> lst = vec.wordsNearest("day", 10);
            System.out.println(lst);
//        UiServer server = UiServer.getInstance();
//        System.out.println("Started on port " + server.getPort());

//            log.info("Save vectors....");
//            WordVectorSerializer.writeWord2VecModel(vec, "pathToSaveModel.txt");

            Collection<String> kingList = vec.wordsNearest(Arrays.asList("king", "woman"), Arrays.asList("queen"), 10);
            System.out.println(kingList);

            Word2Vec word2Vec = WordVectorSerializer.readWord2VecModel("/home/wangyang/workspace/ncbi-genome-download/pathToSaveModel.txt");
            WeightLookupTable weightLookupTable = word2Vec.lookupTable();
            Iterator<INDArray> vectors = weightLookupTable.vectors();
            INDArray wordVectorMatrix = word2Vec.getWordVectorMatrix("myword");
            double[] wordVector = word2Vec.getWordVector("myword");

            System.out.println();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void  trainWord2VecModel(){
        try {
            // 定义文本数据集的路径
            String filePath = "/home/wangyang/workspace/ncbi-genome-download/a.txt";

            // 创建SentenceIterator来读取文本数据集
            SentenceIterator iter = new BasicLineIterator(filePath);

            // 定义分词器工厂
            TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();

            // 构建Word2Vec模型
            Word2Vec word2Vec = new Word2Vec.Builder()
                    .minWordFrequency(5)
                    .iterations(1)
                    .layerSize(100)
                    .seed(42)
                    .windowSize(5)
                    .iterate(iter)
                    .tokenizerFactory(tokenizerFactory)
                    .build();

            // 训练Word2Vec模型
            word2Vec.fit();

            // 保存模型到文件
            WordVectorSerializer.writeWord2VecModel(word2Vec, "/home/wangyang/workspace/ncbi-genome-download/model.bin");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deepLearning1(){
        // 加载预训练的词向量模型
        Word2Vec word2Vec = WordVectorSerializer.readWord2VecModel(new File("/home/wangyang/workspace/ncbi-genome-download/model.bin"));
        // 文本中的菌和疾病
        String bacteria = "microbiota";
        String disease = "metabolic";

        // 获取菌和疾病的词向量表示
        INDArray bacteriaVector = Nd4j.create(word2Vec.getWordVector(bacteria));
        INDArray diseaseVector = Nd4j.create(word2Vec.getWordVector(disease));

        // 计算两个词向量之间的相似度
        double similarity = Transforms.cosineSim(bacteriaVector, diseaseVector);
        System.out.println("菌和疾病之间的相似度：" + similarity);
    }

    public void deepLearning2(){
// Set up the input data and parameters for the Word2Vec model
//        File inputFile = new File("data/corpus.txt");
//        String dataDirectory = inputFile.getParent();
//        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
//        LabelAwareIterator iterator = new LabelAwareFileSentenceIterator(inputFile);
//        AbstractCache<VocabWord> cache = new InMemoryLookupCache.Builder().build();
//        AtomicInteger counter = new AtomicInteger(0);
//        Word2VecConfiguration config = new Word2VecConfiguration.Builder()
//                .learningRate(0.025)
//                .minLearningRate(0.001)
//                .iterations(1)
//                .batchSize(1000)
//                .minWordFrequency(5)
//                .windowSize(5)
//                .layerSize(100)
//                .seed(42)
//                .build();
//
//        // Train the Word2Vec model
//        Word2Vec word2vec = new Word2Vec.Builder()
//                .tokenizerFactory(tokenizerFactory)
//                .iterator(iterator)
//                .vocabCache(cache)
//                .vocabWordFactory(new VocabWordFactory())
//                .configuration(config)
//                .workers(4)
//                .build();
//        word2vec.fit();
//
//        // Use the Word2Vec model to extract the relationship between bacteria and diseases
//        WordVectors wordVectors = new WordVectorsImpl(word2vec.getLookupTable(), cache);
//        double similarity = wordVectors.similarity("bacteria", "disease");
//        System.out.println("Similarity between 'bacteria' and 'disease': " + similarity);
    }

    @Override
    public void getAssociated(){
        PubMed pubMed = pubMedService.findByPMID(36439829);
//
//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // Example text
        String text = "A study found that the bacteria E. coli is associated with food poisoning.";

        // Create an empty Annotation just with the given text
        Annotation document = new Annotation(pubMed.getArticleAbstract());

        // Run all Annotators on this text
        pipeline.annotate(document);

//        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
//        for (CoreMap sentence : sentences) {
//            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
//            for (CoreLabel token : tokens) {
//                String entity = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
//                // 进行实体类型的判断和处理
//                System.out.println();
//            }
//        }


        List<RelationTriple> triples = document.get(CoreAnnotations.SentencesAnnotation.class)
                .stream()
                .flatMap(sentence -> sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class).stream())
                .collect(Collectors.toList());

        for (RelationTriple triple : triples) {
            String subject = triple.subjectGloss();
            String relation = triple.relationGloss();
            String object = triple.objectGloss();
            // 判断关系中是否包含特定菌和疾病
            if (subject.contains("Bacteria") && object.contains("Disease")) {
                // 在这里处理菌和疾病的关系，可以输出、存储或进行其他操作
                System.out.println("菌：" + subject);
                System.out.println("疾病：" + object);
                System.out.println("关系：" + relation);
                System.out.println("----------------------");
            }
            System.out.println();
            // 在这里处理菌和疾病的关系，可以输出、存储或进行其他操作
        }






//        // Iterate over all of the sentences found
//        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
//        for (CoreMap sentence : sentences) {
//            // Iterate over all tokens in a sentence
//            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//                // Extract named entities (NER)
//                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
//                if (ne.equals("ORGANIZATION")) {
//                    String organism = token.originalText();
//                    System.out.println("Organism: " + organism);
//                } else if (ne.equals("O")) {
//                    String word = token.originalText();
//                    System.out.println("Other: " + word);
//                }
//            }
//        }

    }


    @Override
    public void ParagraphVectors(){

        try {
            // 文本数据路径
            String textFilePath = "/home/wangyang/workspace/ncbi-genome-download/raw_sentences.txt";

            // 设置句子迭代器
            SentenceIterator sentenceIterator = new BasicLineIterator(textFilePath);

            // 设置标记化工厂
            TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
            tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

            // 使用Word2Vec模型训练菌群和疾病关系
            int vectorSize = 100; // 词向量大小
            int windowSize = 5; // 窗口大小
            int iterations = 5; // 迭代次数

            Word2Vec word2Vec = new Word2Vec.Builder()
                    .minWordFrequency(5)
                    .iterations(iterations)
                    .layerSize(vectorSize)
                    .seed(42)
                    .windowSize(windowSize)
                    .iterate(sentenceIterator)
                    .tokenizerFactory(tokenizerFactory)
                    .build();

            // 运行Word2Vec模型训练
            word2Vec.fit();

            // 保存训练好的模型
            String modelFilePath = "path/to/save/model.bin";
            WordVectorSerializer.writeWord2VecModel(word2Vec, modelFilePath);

            // 加载已训练好的模型
            Word2Vec loadedModel = WordVectorSerializer.readWord2VecModel(modelFilePath);

            // 获取与菌群相关的词向量
            String microbe = "菌群名称";
            Collection<String> relatedWords = loadedModel.wordsNearest(microbe, 10);

            // 输出与菌群相关的词汇
            System.out.println("与菌群相关的词汇:");
            for (String word : relatedWords) {
                System.out.println(word);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


//        log.info("Load data....");
//        // 加载训练数据集
//        DataSetIterator iter = new RecordReader(new File("train.csv"), 1, 0, 2);
//
//        SentenceIterator iter = new LineSentenceIterator(new File("/home/wangyang/workspace/ncbi-genome-download/raw_sentences.txt"));
//        iter.setPreProcessor(new SentencePreProcessor() {
//            @Override
//            public String preProcess(String sentence) {
//                return sentence.toLowerCase();
//            }
//        });
//
//        // 数据分词
//        // 在每行中按空格分词
//        TokenizerFactory t = new DefaultTokenizerFactory();
//        t.setTokenPreProcessor(new CommonPreprocessor());
//        log.info("Building model....");
//        /**
//         * batchSize是每次处理的词的数量。
//         * minWordFrequency是一个词在语料中必须出现的最少次数。本例中出现不到五次的词都不予学习。词语必须在多种上下文中出现，才能让模型学习到有用的特征。对于规模很大的语料库，理应提高出现次数的下限。
//         * useAdaGrad－AdaGrad为每个特征生成一个不同的梯度。在此处不需要考虑。
//         * layerSize指定词向量中的特征数量，与特征空间的维度数量相等。以500个特征值表示的词会成为一个500维空间中的点。
//         * iterations是网络在处理一批数据时允许更新系数的次数。迭代次数太少，网络可能来不及学习所有能学到的信息；迭代次数太多则会导致网络定型时间变长。
//         * learningRate是每一次更新系数并调整词在特征空间中的位置时的步幅。
//         * minLearningRate是学习速率的下限。学习速率会随着定型词数的减少而衰减。如果学习速率下降过多，网络学习将会缺乏效率。这会使系数不断变化。
//         * iterate告知网络当前定型的是哪一批数据集。
//         * tokenizer将当前一批的词输入网络。
//         * vec.fit()让已配置好的网络开始定型。
//         * */
//        Word2Vec vec = new Word2Vec.Builder()
//                .minWordFrequency(5)
//                .iterations(1)
//                .layerSize(100)
//                .seed(42)
//                .windowSize(5)
//                .iterate(iter)
//                .tokenizerFactory(t)
//                .build();
//
//        log.info("Fitting Word2Vec model....");
//        vec.fit();
//
//        // 使用 Word2Vec 模型创建一个 ParagraphVectors 模型
//        ParagraphVectors paragraphVectors = new ParagraphVectors.Builder()
//                .learningRate(0.025)
//                .minLearningRate(0.001)
//                .batchSize(1000)
//                .epochs(50)
//                .layerSize(100)
//                .trainElementsRepresentation(true)
//                .trainSequencesRepresentation(false)
//                .sampling(0)
//                .useAdaGrad(false)
//                .useHierarchicSoftmax(false)
//                .useUnknown(true)
//                .iterations(1)
//                .workers(4)
//                .seed(42)
//                .elementsLearningAlgorithm(new SkipGram<VocabWord>())
//                .tokenizerFactory(new DefaultTokenizerFactory())
//                .build();
//
//        // 将训练数据集转换为 ParagraphVectors 可以处理的格式
//        List<LabelledDocument> documents = new ArrayList<>();
//        while (iter.hasNext()) {
//            DataSet next = iter.next();
//            String text = next.getFeatures().getString(0);
//            String label = next.getLabels().toString().replaceAll("[\\[\\]]", "");
//            LabelledDocument document = new LabelledDocument();
//            document.setContent(text);
//            document.addLabel(label);
//            documents.add(document);
//        }



    }

    public void CNN(){
        // 1. 加载文献数据集并进行预处理
        List<String> texts = loadTexts("texts.txt");
        List<Integer> labels = loadLabels("labels.txt");

        // 2. 配置模型参数
        int batchSize = 64;
        int nEpochs = 10;
        int nChannels = 1;
        int nClasses = 2;
        int vectorSize = 300;
        int nGrams = 1;
        int filterSize = 128;
        int poolSize = 2;
        int stride = 1;

        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(123)
                .updater(new Adam(0.01))
                .weightInit(WeightInit.XAVIER)
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .list()
                .layer(new ConvolutionLayer.Builder(filterSize, vectorSize)
                        .nIn(nChannels)
                        .stride(stride, 1)
                        .nOut(filterSize)
                        .activation(Activation.RELU)
                        .build())
                .layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(poolSize, 1)
                        .stride(poolSize, 1)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nOut(256)
                        .activation(Activation.RELU)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(nClasses)
                        .activation(Activation.SOFTMAX)
                        .build())
                .setInputType(InputType.convolutionalFlat(1, vectorSize, 1))
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(configuration);
        model.init();

        // 3. 将文本数据转换为特征向量
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        List<List<String>> allTokens = new ArrayList<>();
        for (String text : texts) {
            List<String> tokens = tokenizerFactory.create(text).getTokens();
            allTokens.add(tokens);
        }

        WordVectors wordVectors = WordVectorSerializer.loadStaticModel(new File("word2vec.txt"));

//        WordVectorsTokenizer tokenizer = new WordVectorsTokenizer(wordVectors, allTokens, nGrams);
//        tokenizer.setMaxLength(100);
//
//        DataSetIterator iterator = new ListDataSetIterator<>(new TextDataSetIterator(tokenizer, labels), batchSize);
//
//        // 4. 训练模型
//        for (int i = 0; i < nEpochs; i++) {
//            model.fit(iterator);
//            iterator.reset();
//        }



        // 5. 使用模型进行预测
        String inputText = "The relationship between gut microbiota and inflammatory bowel disease";
        List<String> inputTokens = tokenizerFactory.create(inputText).getTokens();
        INDArray features = wordVectors.getWordVectors(inputTokens).transpose();
        INDArray output = model.output(features);
        System.out.println(output);

    }


    private static List<String> loadTexts(String filePath)  {
        List<String> texts = null;
        try {
            texts = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                texts.add(line);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return texts;
    }

    private static List<Integer> loadLabels(String filePath)  {
        List<Integer> labels = null;
        try {
            labels = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                labels.add(Integer.parseInt(line));
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return labels;
    }
}
