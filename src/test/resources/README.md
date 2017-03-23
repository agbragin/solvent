# Test files description

## Variant Call Format files

### valid_synthetic_with_samples_data.vcf

Synthetic data generated according to [VCF specification](https://samtools.github.io/hts-specs/VCFv4.2.pdf).

### valid_synthetic_without_samples_data.vcf

Synthetic data containing no samples generated according to [VCF specification](https://samtools.github.io/hts-specs/VCFv4.2.pdf).

### valid_metadata_only.vcf

Synthetic file containing no variation data, just header.

### clinvar.vcf

* Downloaded from [NCBI FTP](0:/pub/clinvar/vcf_GRCh37/)
* Filtered for set of regions by SnpSift to make file smaller
* 'chr' prefix added by:

```
sed -r 's/^([^#]+)\t/chr\1\t/g' clinvar.vcf > clinvar.chr.vcf
```

* 'chrMT' records removed by:

```
cat clinvar.chr.vcf | grep -v chrMT > clinvar.vcf
```

### dbsnp.vcf

The same set of actions as for Clinvar.

### 1000genomes.vcf

* First 1000 lines of file chrY_20130502_all.vcf.gz filtered
* 'chr' prefix added

### freebayes.vcf

### gatk.vcf

### isaac.vcf

### samtools.vcf

### snver.vcf

### vardict.vcf
