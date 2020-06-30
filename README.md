# About xhtml5-validator

This repository provides several validation checks on both the structure of the XHTML files and the data inside the files.
This is more than just a schema validator.

It checks the correctness of values in the XHTML file: e.g. `id="..."` attributes are unique, internal links actually point to somewhere in the document.

It [will also](https://github.com/openstax/xhtml-validate) check that the structure of the XHTML is correct. Self-closing tags are a problem for **HTML** parsers so we aim to generate XHTML that can also be parsed by HTML parsers. Self-closing spans, links, headings are particular culprits.


## Details

This performs the following validations:

- [ ] xhtml schema validation using [validator/validator](https://github.com/validator/validator) (just disabled for now)
- [x] unique id attributes
- [x] internal links point to an existing element


# How to Use

Mount a volume and specify the path to the XHTML file as the argument.

```bash
# Clone this repo and run:
docker run --volume $(pwd):/data --rm -it $(docker build -q .) /data/test/fail-duplicate.xhtml
docker run --volume $(pwd):/data --rm -it $(docker build -q .) /data/test/fail-no-link-target.xhtml
docker run --volume $(pwd):/data --rm -it $(docker build -q .) /data/test/pass.xhtml

docker run --volume $(pwd):/data --rm -it $(docker build -q .) /data/test/fail-link-to-duplicate.xhtml link-to-duplicate-id
```

An additional optional argument specifies which test to run:

- none: all the validation checks
- _"duplicate-id"_: check for duplicate id attributes
- _"broken-link"_: check for internal broken links
- _"link-to-duplicate-id"_: duplicate ids are ok (not strictly valid XHTML) but links to duplicates **is** an error
